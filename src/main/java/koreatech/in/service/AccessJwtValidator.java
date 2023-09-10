package koreatech.in.service;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class AccessJwtValidator extends AbstractJwtValidator {
    @Override
    protected SecretKey getKey() {
        return jwtKeyManager.getAccessKey();
    }

    @Override
    protected void validateData(String token, Integer userId) {}

}
