package koreatech.in.service;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;
import static koreatech.in.exception.ExceptionInformation.TOKEN_EXPIRED;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import javax.crypto.SecretKey;
import koreatech.in.exception.BaseException;
import koreatech.in.util.jwt.JwtKeyManager;
import koreatech.in.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractJwtValidator {
    @Autowired
    protected JwtKeyManager jwtKeyManager;

    private final JwtUtil jwtUtil = new JwtUtil();

    public Boolean isExpiredToken(String token) {
        try {
            return jwtUtil.isExpiredToken(token, getKey());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            //보안상 목적으로 상세하게 표현하지 않는다.
            throw new BaseException(BAD_ACCESS);
        }
    }

    public Integer getUserIdInToken(String token) {
        try {
            String subject = jwtUtil.getSubject(token, getKey());

            Integer userId = castToUserId(subject);
            validateData(token, userId);

            return userId;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(TOKEN_EXPIRED);
        }
    }

    protected abstract SecretKey getKey();

    protected abstract void validateData(String token, Integer userId);

    private Integer castToUserId(String subject) throws IllegalStateException {
        try {
            return Integer.parseInt(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("subject를 원하는 타입으로 변환할 수 없습니다.");
        }
    }

}
