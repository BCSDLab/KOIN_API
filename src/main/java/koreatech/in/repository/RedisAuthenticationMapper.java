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
    private static final String DEPRECATED_ACCESS_TOKEN_KEY_NAME = "secretKey";

    private static final String REFRESH_TOKEN_PREFIX = "user:";
    private static final long REFRESH_TOKEN_VALID_DAYS = 14;


    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;


    @Override
    public void setRefreshToken(String refreshToken, Integer userId) {
        stringRedisUtilStr.setDataAsString(REFRESH_TOKEN_PREFIX + userId, refreshToken,
                TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS), TimeUnit.HOURS);
    }

    @Override
    public String getRefreshToken(Integer userId) throws IOException {
        return stringRedisUtilStr.getDataAsString(REFRESH_TOKEN_PREFIX + userId);
    }

    @Override
    public void deleteRefreshToken(Integer userId) {
        stringRedisUtilStr.deleteData(REFRESH_TOKEN_PREFIX + userId);
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
