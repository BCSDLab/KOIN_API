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
public abstract class JwtManager<T> {
    @Autowired
    protected JwtKeyManager jwtKeyManager;

    public abstract String generate(T data);

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

    public T getDataFromToken(String token) {
        try {
            T data = getData(token);
            validateData(token, data);

            return data;
        } catch (JwtException | IllegalArgumentException e){
            throw new BaseException(TOKEN_EXPIRED);
        }
    }

    protected Date makeExpiration() {
        return DateUtil.addHoursToJavaUtilDate(new Date(), (int) getExpirationHour());
    }

    protected abstract long getExpirationHour();

    protected abstract SecretKey getKey();

    private T getData(String token) throws IllegalArgumentException, JwtException {
        Claims body = Jwts.parser()
                .setSigningKey(getKey())
                .parseClaimsJws(token)
                .getBody();

        return castToData(body.getSubject());
    }
    abstract protected T castToData(String subject) throws IllegalStateException;

    //e.g. Redis의 것과 비교 (optional)
    protected void validateData(String token, T data) {
    }
}
