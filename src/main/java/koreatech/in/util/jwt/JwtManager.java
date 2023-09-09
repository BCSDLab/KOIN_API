package koreatech.in.util.jwt;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;
import static koreatech.in.exception.ExceptionInformation.TOKEN_EXPIRED;

import io.jsonwebtoken.JwtException;
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

    private final JwtUtil jwtUtil = new JwtUtil();

    public String generate(T data) {
        SecretKey key = getKey();
        return jwtUtil.generateToken(castToSubject(data), makeExpiration(), key);
    }

    public Boolean isExpired(String token) {
        try {
            return jwtUtil.isExpiredToken(token, getKey());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            //보안상 목적으로 상세하게 표현하지 않는다.
            throw new BaseException(BAD_ACCESS);
        }
    }

    public T getDataFromToken(String token) {
        try {
            String subject = jwtUtil.getSubject(token, getKey());

            T data = castToData(subject);
            validateData(token, data);

            return data;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(TOKEN_EXPIRED);
        }
    }

    protected abstract String castToSubject(T data);

    protected Date makeExpiration() {
        return DateUtil.addHoursToJavaUtilDate(new Date(), (int) getExpirationHour());
    }

    protected abstract long getExpirationHour();

    protected abstract SecretKey getKey();

    abstract protected T castToData(String subject) throws IllegalStateException;

    //e.g. Redis의 것과 비교 (optional)
    protected void validateData(String token, T data) {
    }
}
