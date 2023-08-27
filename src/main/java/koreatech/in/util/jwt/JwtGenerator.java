package koreatech.in.util.jwt;

import static koreatech.in.exception.ExceptionInformation.TOKEN_EXPIRED;
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
public abstract class JwtGenerator<T> {
    @Autowired
    protected JwtKeyManager jwtKeyManager;

    protected abstract long getTokenValidHour();

    protected Date makeExpiration() {
        return DateUtil.addHoursToJavaUtilDate(new Date(), (int) getTokenValidHour());
    }

    protected abstract SecretKey getKey();

    public T getDataFromToken(String token) {
        try {
            T data = getData(token);
            validateData(token, data);

            return data;
        } catch (JwtException | IllegalArgumentException e){
            throw new BaseException(TOKEN_EXPIRED);
        }
    }

    //e.g. Redis의 것과 비교 (구현 = optional)
    protected void validateData(String token, T data) {
    }

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
            //보안상 목적으로 상세하게 표현하지 않는다.
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public abstract String generateToken(T data);
}
