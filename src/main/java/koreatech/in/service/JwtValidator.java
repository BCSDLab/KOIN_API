package koreatech.in.service;

import koreatech.in.domain.Authority;
import koreatech.in.domain.User.User;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.UserMapper;
import koreatech.in.util.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class JwtValidator {
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    public User validate() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String header = request.getHeader("Authorization");

        return validate(header);
    }

    public User validate(String header) {
        if (header == null || !header.startsWith("Bearer "))
            return null;

        String authToken = header.substring(7);
        if (authToken.equals("undefined")) // 추후 프론트엔드 측에서 변경
            return null;

        int userId = jwtTokenGenerator.me(authToken);

        User user;
        Integer identity = userMapper.getUserIdentity(userId);
        if (identity == null) return null;
        switch (identity) {
            case 4:
                user = userMapper.getOwner(userId);
                break;
            case 0: case 1: case 2: case 3: default:
                user = userMapper.getUser(userId);
                break;
        }

        if (user == null) return null;

        Authority authority = authorityMapper.getAuthorityByUserId(user.getId());

        user.setAuthority(authority);

        return user;
    }
}
