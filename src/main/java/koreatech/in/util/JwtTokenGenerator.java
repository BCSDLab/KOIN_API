package koreatech.in.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.UnauthorizeException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Date;

@NoArgsConstructor
public class JwtTokenGenerator {
    @Autowired
    private StringRedisUtilObj stringRedisUtilObj;

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private SecretKeySpec key;

    @PostConstruct
    public void keySetter() throws IOException {
        try {
            key = (SecretKeySpec) stringRedisUtilObj.getDataAsString("secretKey", SecretKeySpec.class);
            if (key == null) {
                key = (SecretKeySpec) Keys.secretKeyFor(signatureAlgorithm);
                stringRedisUtilObj.setDataAsString("secretKey", key);
            }
        } catch (IOException | IllegalArgumentException e) {
            key = (SecretKeySpec) Keys.secretKeyFor(signatureAlgorithm);
            stringRedisUtilObj.setDataAsString("secretKey", key);
        }
    }

    public String generate(int id) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), 72);
        return Jwts.builder().setSubject(String.valueOf(id)).setExpiration(exp).signWith(this.key).compact();
    }

    public int me(String token) {
        try {
            Claims body = Jwts.parser().setSigningKey(this.key).parseClaimsJws(token).getBody();
            int userId = Integer.parseInt(body.getSubject());
            String redisToken = (String) stringRedisUtilObj.getDataAsString("user@" + userId, String.class);
            if (!token.equals(redisToken)) {
                throw new UnauthorizeException(new ErrorMessage("토큰이 변경되었습니다. 다시 로그인해주세요.", 0));
            }
            return userId;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizeException(new ErrorMessage("잘못된 접근입니다.", 0));
        } catch (ExpiredJwtException | IOException e) {
            throw new UnauthorizeException(new ErrorMessage("토큰이 만료되었습니다. 다시 로그인해주세요.", 0));
        }
    }

    public Boolean isExpired(String token) {
        try {
            Jwts.parser().setSigningKey(this.key).parseClaimsJws(token);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizeException(new ErrorMessage("잘못된 접근입니다.", 0));
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
