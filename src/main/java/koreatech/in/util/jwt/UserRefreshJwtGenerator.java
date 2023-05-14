package koreatech.in.util.jwt;


import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.repository.AuthenticationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRefreshJwtGenerator extends AbstractJwtGenerator<Integer> {
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;

    @Autowired
    private AuthenticationMapper redisAuthenticationMapper;

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
        return TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS);
    }

    private String makeSubject(Integer data) {
        return String.valueOf(data);
    }

    @Override
    protected final SecretKey getKey() {
        return super.jwtKeyManager.getRefreshKey();
    }

    @Override
    protected void validateData(String token,  Integer data) {
        try {
            String tokenInRedis = redisAuthenticationMapper.getRefreshToken(data);
            if (token.equals(tokenInRedis)) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new BaseException(ExceptionInformation.BAD_ACCESS);
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
