package koreatech.in.util.jwt;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class UserAccessJwtGenerator extends AbstractJwtGenerator<Integer> {
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 72;

    @Override
    protected long getTokenValidHour() {
        return PREV_ACCESS_TOKEN_VALID_HOUR;
    }

    @Override
    protected String makeSubject(Integer data) {
        return String.valueOf(data);
    }

    @Override
    protected SecretKey getKey() {
        return super.jwtKeyManager.getAccessKey();
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
