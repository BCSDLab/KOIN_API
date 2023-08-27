package koreatech.in.service;

import javax.servlet.http.HttpServletRequest;
import koreatech.in.domain.User.User;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.jwt.TemporaryAccessJwtManager;
import koreatech.in.util.jwt.UserAccessJwtManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
public class JwtValidator {
    private static final String AUTH_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private UserAccessJwtManager userAccessJwtGenerator;
    @Autowired
    private TemporaryAccessJwtManager temporaryAccessJwtGenerator;
    @Autowired
    private UserMapper userMapper;

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

    public boolean isValidTemporaryAccessTokenInHeader(String header) {
        if(!isAuthHeader(header)) {
            return false;
        }
        return !temporaryAccessJwtGenerator.isExpired(makeTokenFromHeader(header));
    }

    private Integer getUserId(String header) {
        if (!isAuthHeader(header)) {
            return null;
        }

        String accessToken = makeTokenFromHeader(header);
        if (accessToken.equals("undefined")) { // 추후 프론트엔드 측에서 변경
            return null;
        }

        return userAccessJwtGenerator.getDataFromToken(accessToken);
    }

    private boolean isAuthHeader(String header) {
        return header != null && header.startsWith(AUTH_PREFIX);
    }

    private String makeTokenFromHeader(String header) {
        return header.substring(AUTH_PREFIX.length());
    }

    public Integer validateAndGetUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        return getUserId(request.getHeader(AUTHORIZATION));
    }
}
