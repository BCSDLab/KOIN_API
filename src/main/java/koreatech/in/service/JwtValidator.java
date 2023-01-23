package koreatech.in.service;

import koreatech.in.domain.User.User;
import koreatech.in.exception.BaseException;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;

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
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        String accessToken = header.substring(7);
        if (accessToken.equals("undefined")) { // 추후 프론트엔드 측에서 변경
            return null;
        }

        Integer userId = jwtTokenGenerator.me(accessToken);
        User user = userMapper.getAuthedUserById(userId);

        return user;
    }
}
