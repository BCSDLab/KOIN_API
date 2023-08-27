package koreatech.in.util.jwt;

import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class TemporaryAccessJwtManager extends JwtManager<Void> {
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 2;

    @Override
    public String generate(Void data) {
        return Jwts.builder()
                .setExpiration(makeExpiration())
                .signWith(getKey())
                .compact();
    }

    @Override
    protected long getExpirationHour() {
        return PREV_ACCESS_TOKEN_VALID_HOUR;
    }

    @Override
    protected SecretKey getKey() {
        return super.jwtKeyManager.getAccessKey();
    }

    @Override
    protected Void castToData(String subject) throws IllegalStateException {
        return null;
    }

}
