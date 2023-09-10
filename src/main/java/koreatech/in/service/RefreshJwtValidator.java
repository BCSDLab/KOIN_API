package koreatech.in.service;

import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.crypto.SecretKey;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.repository.AuthenticationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshJwtValidator extends AbstractJwtValidator {
    @Autowired
    private AuthenticationMapper authenticationMapper;

    @Override
    protected SecretKey getKey() {
        return jwtKeyManager.getRefreshKey();
    }

    @Override
    protected void validateData(String token, Integer userId) {
        try {
            String tokenInRedis = authenticationMapper.getRefreshToken(userId);
            if (!token.equals(tokenInRedis)) {
                throw new BaseException(ExceptionInformation.TOKEN_EXPIRED);
            }
        } catch (IOException e) {
            throw new ExpiredJwtException(null, null, null, e);
        }
    }

}
