package koreatech.in.repository;

import koreatech.in.domain.RedisOwnerKeyPrefix;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerInVerification;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.util.StringRedisUtilObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisOwnerMapper {

    @Autowired
    private StringRedisUtilObj stringRedisUtilObj;

    public void changeAuthStatus(OwnerInCertification ownerInCertification, String email, RedisOwnerKeyPrefix redisOwnerKeyPrefix) {
        String key = redisOwnerKeyPrefix.getKey(email);
        OwnerInVerification ownerInRedis = getOwner(key);

        ownerInRedis.validateFor(ownerInCertification);
        ownerInRedis.setIs_authed(true);

        putRedisFor(key, ownerInRedis);
    }

    public OwnerInVerification getOwner(String key) {
        Object json;
        try {
            json = stringRedisUtilObj.getDataAsString(key, OwnerInVerification.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        OwnerInVerification ownerInRedis = (OwnerInVerification) json;
        validateRedis(ownerInRedis);

        return ownerInRedis;
    }

    private static void validateRedis(OwnerInVerification ownerInRedis) {
        if (ownerInRedis == null) {
            throw new BaseException(ExceptionInformation.EMAIL_ADDRESS_SAVE_EXPIRED);
        }
        ownerInRedis.validateFields();
    }

    public void putRedisFor(String ownerKey, OwnerInVerification ownerInVerification) {
        try {
            stringRedisUtilObj.setDataAsString(ownerKey,
                    ownerInVerification, 2L, TimeUnit.HOURS);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void validateOwner(EmailAddress emailAddress, RedisOwnerKeyPrefix redisOwnerKeyPrefix) {
        String email = emailAddress.getEmailAddress();
        OwnerInVerification ownerInRedis = getOwner(redisOwnerKeyPrefix.getKey(email));
        ownerInRedis.validateCertificationComplete();
    }

    public void removeRedisFrom(EmailAddress emailAddress, RedisOwnerKeyPrefix redisOwnerKeyPrefix) {
        stringRedisUtilObj.deleteData(redisOwnerKeyPrefix.getKey(emailAddress.getEmailAddress()));
    }

    public void removeRedisFrom(String ownerKey) {
        stringRedisUtilObj.deleteData(ownerKey);
    }
}
