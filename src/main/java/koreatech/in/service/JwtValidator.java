package koreatech.in.service;

import koreatech.in.domain.Authority;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.user.User;
import koreatech.in.domain.user.UserCode;
import koreatech.in.domain.user.UserType;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.ValidationException;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserMapper userMapper;
    private final AuthorityMapper authorityMapper;
    private final OwnerMapper ownerMapper;

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

        Long userId = (long)jwtTokenGenerator.me(authToken);

        User user = userMapper.getUserById(userId)
                .orElseThrow(()->new ValidationException(new ErrorMessage("token not validate", 402)));

        Authority authority = authorityMapper.getAuthorityByUserId(user.getId());

        user.setAuthority(authority);

        return user;
    }
}
