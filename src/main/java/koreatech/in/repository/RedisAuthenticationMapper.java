package koreatech.in.repository;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class RedisAuthenticationMapper implements AuthenticationMapper {
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;

    public static final String DEPRECATED_ACCESS_TOKEN_KEY_NAME = "secretKey";

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    private static final String redisLoginTokenKeyPrefix = "user:";

    @Override
    public void setRefreshToken(String refreshToken, Integer userId) {
        stringRedisUtilStr.setDataAsString(redisLoginTokenKeyPrefix + userId, refreshToken,
                TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS), TimeUnit.HOURS);
    }

    @Override
    public String getRefreshToken(Integer userId) throws IOException {
        return stringRedisUtilStr.getDataAsString(redisLoginTokenKeyPrefix + userId);
    }

    @Override
    public void deleteRefreshToken(Integer userId) {
        stringRedisUtilStr.deleteData(redisLoginTokenKeyPrefix + userId);
    }

    @Override
    public Optional<String> getDeprecatedAccessTokenKey() {
        try {
            return Optional.ofNullable(stringRedisUtilStr.getDataAsString(DEPRECATED_ACCESS_TOKEN_KEY_NAME));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

}
