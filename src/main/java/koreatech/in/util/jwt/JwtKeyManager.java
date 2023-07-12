package koreatech.in.util.jwt.key;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import koreatech.in.domain.Auth.JWTKeys;
import koreatech.in.repository.AuthenticationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtKeyManager {
    //TODO AccessKeyManager, RefreshKeyManager로 분리하기.  (KeyManager Interface -> AbastractKeyManager -> {AccKeyMangner, RefKeyManager}
    public static final String ACCESS_KEY_FIELD_NAME = "access_key";
    public static final String REFRESH_KEY_FIELD_NAME = "refresh_key";
    private static final String SECRET_KEY_COLLECTION = "secret_key";
    public static final int OFFSET = 0;

    @Autowired
    private AuthenticationMapper redisAuthenticationMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private JWTKeys jwtKeys;

    public SecretKey getAccessKey() {
        return this.jwtKeys.getAccessKey();
    }

    public SecretKey getRefreshKey() {
        return this.jwtKeys.getRefreshKey();
    }

    @PostConstruct
    public void KeySetter() {
        this.jwtKeys = getKeys();
    }

    private JWTKeys getKeys() {
        DBCollection secretKeyCollection = mongoTemplate.getCollection(SECRET_KEY_COLLECTION);
        Optional<DBObject> jwtKeysInDBOptional = Optional.ofNullable(secretKeyCollection.findOne());

        if (!jwtKeysInDBOptional.isPresent()) {
            return createKeys(secretKeyCollection);
        }

        if (needAnyKeyUpdate(jwtKeysInDBOptional.get())) {
            return updateKeys(secretKeyCollection, jwtKeysInDBOptional.get());
        }

        return getKeys(jwtKeysInDBOptional.get());
    }

    private JWTKeys createKeys(DBCollection secretKeyCollection) {
        JWTKeys newJwtKeys = JWTKeys.of(
                createKey(ACCESS_KEY_FIELD_NAME),
                createKey(REFRESH_KEY_FIELD_NAME)
        );

        secretKeyCollection.insert(toDBObjectWithEncode(newJwtKeys));
        return newJwtKeys;
    }

    private boolean needAnyKeyUpdate(DBObject jwtKeysInDB) {
        return needKeyCreate(jwtKeysInDB, ACCESS_KEY_FIELD_NAME) || needKeyCreate(jwtKeysInDB, REFRESH_KEY_FIELD_NAME);
    }

    private JWTKeys updateKeys(DBCollection secretKeyCollection, DBObject jwtKeysInDB) {
        JWTKeys updatedKeys = JWTKeys.of(
                updateKey(jwtKeysInDB, ACCESS_KEY_FIELD_NAME),
                updateKey(jwtKeysInDB, REFRESH_KEY_FIELD_NAME)
        );
        secretKeyCollection.update(new BasicDBObject(), toDBObjectWithEncode(updatedKeys), true, false);

        return updatedKeys;
    }

    private JWTKeys getKeys(DBObject jwtKeysInDB) {
        String accessKey = (String) jwtKeysInDB.get(ACCESS_KEY_FIELD_NAME);
        String refreshKey = (String) jwtKeysInDB.get(REFRESH_KEY_FIELD_NAME);

        return JWTKeys.of(decode(accessKey), decode(refreshKey));
    }

    private SecretKey createKey(String keyFieldName) {
        try {
            return decode(redisAuthenticationMapper.getDeprecatedKey(keyFieldName));
        } catch (IOException exception) {
            return Keys.secretKeyFor(signatureAlgorithm);
        }
    }

    private SecretKey updateKey(DBObject jwtKeysInDB, String keyFieldName) {
        if (needKeyCreate(jwtKeysInDB, keyFieldName)) {
            return createKey(keyFieldName);
        }

        return decode((String) jwtKeysInDB.get(keyFieldName));
    }

    private boolean needKeyCreate(DBObject jwtKeysInDB, String fieldName) {
        if (!(jwtKeysInDB.containsField(fieldName))) {
            return true;
        }

        Object fieldKey = jwtKeysInDB.get(fieldName);
        return !(fieldKey instanceof String) || ((String) fieldKey).isEmpty();
    }

    private static String encode(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    private static SecretKey decode(String encodedKey) {
        if (!StringUtils.hasText(encodedKey)) {
            return null;
        }
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, OFFSET, decodedKey.length, signatureAlgorithm.getJcaName());
    }

    private DBObject toDBObjectWithEncode(JWTKeys jwtKeys) {
        return BasicDBObjectBuilder.start()
                .add(ACCESS_KEY_FIELD_NAME, encode(jwtKeys.getAccessKey()))
                .add(REFRESH_KEY_FIELD_NAME, encode(jwtKeys.getRefreshKey()))
                .get();
    }

}
