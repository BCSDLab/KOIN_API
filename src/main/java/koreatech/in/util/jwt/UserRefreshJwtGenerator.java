package koreatech.in.util.jwt;


import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class UserRefreshJwtGenerator extends AbstractJwtGenerator<Integer> {
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;

    @Override
    protected long getTokenValidHour() {
        return TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS);
    }

    @Override
    protected String makeSubject(Integer data) {
        return String.valueOf(data);
    }

    @Override
    protected SecretKey getKey() {
        return super.jwtKeyManager.getRefreshKey();
    }

    @Override
    protected void validateData(Integer data) {

    }

    @Override
    protected Integer toData(String subject) throws IllegalStateException {
        try {
            return Integer.parseInt(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("subject를 원하는 타입으로 변환할 수 없습니다.");
        }
    }
}
