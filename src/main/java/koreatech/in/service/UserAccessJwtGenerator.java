package koreatech.in.service;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class UserAccessJwtGenerator extends JwtGenerator<Integer> {
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 72;

    protected String castToSubject(Integer data) {
        return String.valueOf(data);
    }

    @Override
    protected long getExpirationHour() {
        return PREV_ACCESS_TOKEN_VALID_HOUR;
    }

    @Override
    protected SecretKey getKey() {
        return super.jwtKeyManager.getAccessKey();
    }

}
