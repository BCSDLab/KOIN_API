package koreatech.in.repository;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class RedisAuthenticationMapper implements AuthenticationMapper {
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;
    private static final long PREV_ACCESS_TOKEN_VALID_HOUR = 72;
    private static final long ACCESS_TOKEN_VALID_HOUR = 2;

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Value("${redis.key.login_prefix}")
    private String redisLoginTokenKeyPrefix;

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
}
