package koreatech.in.util.jwt;


import io.jsonwebtoken.ExpiredJwtException;
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
public class UserRefreshJwtManager extends JwtManager<Integer> {
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    @Override
    public String generate(Integer data) {
        return Jwts.builder()
                .setSubject(makeSubject(data))
                .setExpiration(makeExpiration())
                .signWith(getKey())
                .compact();
    }

    private String makeSubject(Integer data) {
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

    @Override
    protected void validateData(String token,  Integer data) {
        try {
            String tokenInRedis = authenticationMapper.getRefreshToken(data);
            if (!token.equals(tokenInRedis)) {
                throw new BaseException(ExceptionInformation.TOKEN_EXPIRED);
            }
        } catch (IOException e) {
            throw new ExpiredJwtException(null, null, null, e);
        }
    }

}
