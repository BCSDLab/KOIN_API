package koreatech.in.util.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class TemporaryAccessJwtGenerator extends AccessJwtGenerator<Void> {
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 2;

    @Override
    protected long getTokenValidHour() {
        return PREV_ACCESS_TOKEN_VALID_HOUR;
    }

    @Override
    protected Void toData(String subject) throws IllegalStateException {
        return null;
    }

    @Override
    public String generateToken(Void data) {
        return Jwts.builder()
                .setExpiration(makeExpiration())
                .signWith(getKey())
                .compact();
    }

}