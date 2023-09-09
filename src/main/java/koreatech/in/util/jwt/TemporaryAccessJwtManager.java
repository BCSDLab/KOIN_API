package koreatech.in.util.jwt;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class TemporaryAccessJwtManager extends JwtManager<Void> {
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
