package koreatech.in.service;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;
import static koreatech.in.exception.ExceptionInformation.TOKEN_EXPIRED;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import koreatech.in.domain.User.User;
import koreatech.in.exception.BaseException;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.jwt.JwtKeyManager;
import koreatech.in.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class JwtValidator {
    private static final String AUTH_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    protected JwtKeyManager jwtKeyManager;

    @Autowired
    private UserMapper userMapper;

    private final JwtUtil jwtUtil = new JwtUtil();

    public User validate() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String header = request.getHeader(AUTHORIZATION);

        return validate(header);
    }

    public User validate(String header) {
        Integer userId = getUserId(header);
        if (userId == null) {
            return null;
        }

        return userMapper.getAuthedUserById(userId);
    }

    public Integer validateAndGetUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        return getUserId(request.getHeader(AUTHORIZATION));
    }

    public void validateTemporaryAccessTokenInHeader(String header) {
        validateToken(makeTokenFromHeader(header), getKey());
    }

    private Integer getUserId(String header) {
        if (!isAuthHeader(header)) {
            return null;
        }

        String accessToken = makeTokenFromHeader(header);
        if (accessToken.equals("undefined")) { // 추후 프론트엔드 측에서 변경
            return null;
        }
        String subject = jwtUtil.getSubject(accessToken, getKey());
        return Integer.parseInt(subject);
    }

    private boolean isAuthHeader(String header) {
        return header != null && header.startsWith(AUTH_PREFIX);
    }

    private String makeTokenFromHeader(String header) {
        return header.substring(AUTH_PREFIX.length());
    }

    private void validateToken(String token, SecretKey key) {
        try {
            if (jwtUtil.isExpiredToken(token, key)) {
                throw new BaseException(TOKEN_EXPIRED);
            }
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            //보안상 목적으로 상세하게 표현하지 않는다.
            throw new BaseException(BAD_ACCESS);
        }
    }

    private SecretKey getKey() {
        return jwtKeyManager.getAccessKey();
    }

}
