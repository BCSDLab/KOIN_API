package koreatech.in.util.jwt;

import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class UserRefreshJwtManager extends JwtManager<Integer> {
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;

    @Override
    protected String castToSubject(Integer data) {
        return String.valueOf(data);
    }

    @Override
    protected long getExpirationHour() {
        return TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS);
    }

    @Override
    protected final SecretKey getKey() {
        return super.jwtKeyManager.getRefreshKey();
    }

}
