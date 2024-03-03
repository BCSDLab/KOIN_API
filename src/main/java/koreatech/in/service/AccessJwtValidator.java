package koreatech.in.service;

import javax.crypto.SecretKey;
import koreatech.in.domain.User.User;
import koreatech.in.repository.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessJwtValidator extends AbstractJwtValidator {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected SecretKey getKey() {
        return jwtKeyManager.getAccessKey();
    }

    @Override
    protected void validateData(String token, Integer userId) {}

    public User validateAndGetUserFromAccessToken(String accessToken) {
        Integer userId = getUserIdInToken(accessToken);
        return userMapper.getAuthedUserById(userId);
    }
}