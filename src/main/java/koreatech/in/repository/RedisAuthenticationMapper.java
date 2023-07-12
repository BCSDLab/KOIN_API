package koreatech.in.repository;

import java.io.IOException;
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

    public String getDeprecatedKey(String keyName) throws IOException {
        if(!JwtKeyManager.REFRESH_KEY_FIELD_NAME.equals(keyName)) {
            throw new IOException("Refresh Key는 Redis에 저장되어있지 않습니다.");
        }

        return stringRedisUtilStr.getDataAsString(SECRET_KEY);
    }

}
