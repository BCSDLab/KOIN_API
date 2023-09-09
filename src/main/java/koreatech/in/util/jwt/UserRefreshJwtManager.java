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

    @Override
    protected Integer castToData(String subject) throws IllegalStateException {
        try {
            return Integer.parseInt(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("subject를 원하는 타입으로 변환할 수 없습니다.");
        }
    }

}
