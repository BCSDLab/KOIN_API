package koreatech.in.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.UnauthorizeException;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.Key;
import java.util.Date;

public class JwtTokenGenerator {
    private Key key;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Key> valueOpsStrKey;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOpsStrStr;

    public JwtTokenGenerator() { }

    @PostConstruct
    public void keySetter() {
        key = valueOpsStrKey.get("secretKey");
        if (key == null) {
            key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            valueOpsStrKey.set("secretKey", key);
        }
    }

    public String generate(int id) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), 72);

        return Jwts.builder().setSubject(String.valueOf(id)).setExpiration(exp).signWith(key).compact();
    }

    public int me(String token) {
        try {
            Claims body = Jwts.parser().setSigningKey(this.key).parseClaimsJws(token).getBody();
            int userId = Integer.parseInt(body.getSubject());
            String redisToken = valueOpsStrStr.get("user@" + userId);
            if (!token.equals(redisToken)) {
                throw new UnauthorizeException(new ErrorMessage("토큰이 변경되었습니다. 다시 로그인해주세요.", 0));
            }
            return userId;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizeException(new ErrorMessage("잘못된 접근입니다.", 0));
        } catch (ExpiredJwtException e) {
            throw new UnauthorizeException(new ErrorMessage("토큰이 만료되었습니다.", 0));
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
