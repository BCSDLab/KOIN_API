package koreatech.in.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
    private ValueOperations<String, Key> valueOps;

    public JwtTokenGenerator() { }

    @PostConstruct
    public void keySetter() {
        key = valueOps.get("secretKey");
        if (key == null) {
            key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            valueOps.set("secretKey", key);
        }
    }

    public String generate(int id) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), 72);

        return Jwts.builder().setSubject(String.valueOf(id)).setExpiration(exp).signWith(key).compact();
    }

    public int me(String token) {
        try {
            Claims body = Jwts.parser().setSigningKey(this.key).parseClaimsJws(token).getBody();

            Date expiredAt = body.getExpiration();

            if(expiredAt.getTime() - new Date().getTime() < 0) {
                throw new UnauthorizeException(new ErrorMessage("Invalid JWT", 0));
            }

            return Integer.parseInt(body.getSubject());
        } catch (Exception e) {
            throw new UnauthorizeException(new ErrorMessage("Invalid JWT", 0));
        }
    }

    public Boolean isExpired(String token) {
        try {
            Claims body = Jwts.parser().setSigningKey(this.key).parseClaimsJws(token).getBody();

            Date expiredAt = body.getExpiration();

            if(expiredAt.getTime() - new Date().getTime() < 0) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }
}
