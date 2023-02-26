package koreatech.in.service;

import javax.servlet.http.HttpServletRequest;
import koreatech.in.domain.User.User;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
public class JwtValidator {
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private UserMapper userMapper;

    public User validate() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String header = request.getHeader("Authorization");

        return validate(header);
    }

    public User validate(String header) {
        Integer userId = getUserId(header);
        if (userId == null) {
            return null;
        }

        return userMapper.getAuthedUserById(userId);
    }

    private Integer getUserId(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        String accessToken = header.substring(7);
        if (accessToken.equals("undefined")) { // 추후 프론트엔드 측에서 변경
            return null;
        }

        return jwtTokenGenerator.me(accessToken);
    }

    public Integer validateAndGetUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        return getUserId(request.getHeader("Authorization"));
    }
}
