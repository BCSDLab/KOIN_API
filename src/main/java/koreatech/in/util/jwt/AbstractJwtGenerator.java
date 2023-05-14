package koreatech.in.util.jwt;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import koreatech.in.exception.BaseException;
import koreatech.in.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractJwtGenerator<T> implements JwtGenerator<T> {
    @Autowired()
    protected JwtKeyManager jwtKeyManager;

    protected abstract long getTokenValidHour();

    abstract protected String makeSubject(T data);

    public String generateToken(T data) {
        return Jwts.builder()
                .setSubject(makeSubject(data))
                .setExpiration(makeExpiration())
                .signWith(getKey())
                .compact();
    }

    private Date makeExpiration() {
        return DateUtil.addHoursToJavaUtilDate(new Date(), (int) getTokenValidHour());
    }

    protected abstract SecretKey getKey();

    public T getFromToken(String token) {
        T data;
        try {
            data = getData(token);
            validateData(data);
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new BaseException(BAD_ACCESS);
        }

        return data;
    }

    //e.g. Redis의 것과 비교 (구현 = optional)
    abstract protected void validateData(T data);

    private T getData(String token) throws IllegalArgumentException, JwtException {
        Claims body = Jwts.parser()
                .setSigningKey(getKey())
                .parseClaimsJws(token)
                .getBody();

        return toData(body.getSubject());
    }
    abstract protected T toData(String subject) throws IllegalStateException;

    public Boolean isExpired(String token) {
        try {
            Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
