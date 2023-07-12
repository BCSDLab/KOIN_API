package koreatech.in.repository;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import koreatech.in.util.StringRedisUtilStr;
import koreatech.in.util.jwt.JwtKeyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RedisAuthenticationMapper implements AuthenticationMapper {
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;

    public static final String SECRET_KEY = "secretKey";

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    private static final String redisLoginTokenKeyPrefix = "user:";

    public void setRefreshToken(String refreshToken, Integer userId) {
        stringRedisUtilStr.setDataAsString(redisLoginTokenKeyPrefix + userId, refreshToken,
                TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS), TimeUnit.HOURS);
    }

    public String getRefreshToken(Integer userId) throws IOException {
        return stringRedisUtilStr.getDataAsString(redisLoginTokenKeyPrefix + userId);
    }

    @Override
    public void deleteRefreshToken(Integer userId) {
        stringRedisUtilStr.deleteData(redisLoginTokenKeyPrefix + userId);
    }

    public Optional<String> getDeprecatedKey(String keyName) {
        if (JwtKeyManager.REFRESH_KEY_FIELD_NAME.equals(keyName)) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(stringRedisUtilStr.getDataAsString(SECRET_KEY));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

}
