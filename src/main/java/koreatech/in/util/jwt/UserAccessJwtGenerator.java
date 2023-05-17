package koreatech.in.util.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class UserAccessJwtGenerator extends AccessJwtGenerator<Integer> {
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 72;

    @Override
    public String generateToken(Integer data) {
        return Jwts.builder()
                .setSubject(makeSubject(data))
                .setExpiration(makeExpiration())
                .signWith(getKey())
                .compact();
    }

    @Override
    protected long getTokenValidHour() {
        return PREV_ACCESS_TOKEN_VALID_HOUR;
    }

    protected String makeSubject(Integer data) {
        return String.valueOf(data);
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
