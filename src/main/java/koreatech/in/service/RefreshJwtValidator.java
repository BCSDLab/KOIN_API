package koreatech.in.service;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;
import static koreatech.in.exception.ExceptionInformation.TOKEN_EXPIRED;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import javax.crypto.SecretKey;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.repository.AuthenticationMapper;
import koreatech.in.util.jwt.JwtKeyManager;
import koreatech.in.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshJwtValidator {
    @Autowired
    protected JwtKeyManager jwtKeyManager;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    private final JwtUtil jwtUtil = new JwtUtil();

    public Boolean isExpiredToken(String token) {
        try {
            return jwtUtil.isExpiredToken(token, getKey());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            //보안상 목적으로 상세하게 표현하지 않는다.
            throw new BaseException(BAD_ACCESS);
        }
    }

    public Integer getDataFromToken(String token) {
        try {
            String subject = jwtUtil.getSubject(token, getKey());

            Integer data = castToData(subject);
            validateData(token, data);

            return data;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(TOKEN_EXPIRED);
        }
    }

    private SecretKey getKey() {
        return jwtKeyManager.getRefreshKey();
    }

    private Integer castToData(String subject) throws IllegalStateException {
        try {
            return Integer.parseInt(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("subject를 원하는 타입으로 변환할 수 없습니다.");
        }
    }

    private void validateData(String token, Integer data) {
        try {
            String tokenInRedis = authenticationMapper.getRefreshToken(data);
            if (!token.equals(tokenInRedis)) {
                throw new BaseException(ExceptionInformation.TOKEN_EXPIRED);
            }
        } catch (IOException e) {
            throw new ExpiredJwtException(null, null, null, e);
        }
    }

}
