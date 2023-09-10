package koreatech.in.service;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class TemporaryAccessJwtGenerator extends JwtGenerator<Void> {
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 2;

    @Override
    protected String castToSubject(Void data) {
        return null;
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
