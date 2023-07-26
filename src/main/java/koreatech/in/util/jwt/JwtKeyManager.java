package koreatech.in.util.jwt;

import static koreatech.in.repository.RedisAuthenticationMapper.SECRET_KEY;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
    private static final String ACCESS_KEY_FIELD_NAME = "access_key";
    private static final String REFRESH_KEY_FIELD_NAME = "refresh_key";
    private static final String SECRET_KEY_COLLECTION = "secret_key";

    private static final int OFFSET = 0;

    @Autowired
    private AuthenticationMapper authenticationMapper;

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
        Optional<DBObject> jwtKeys = Optional.ofNullable(secretKeyCollection.findOne());

        if (!jwtKeys.isPresent()) {
            return createKeys(secretKeyCollection);
        }

        if (needAnyKeyUpdate(jwtKeys.get())) {
            return updateKeys(secretKeyCollection, jwtKeys.get());
        }

        return getKeys(jwtKeys.get());
    }

    private JWTKeys createKeys(DBCollection secretKeyCollection) {
        JWTKeys newJwtKeys = JWTKeys.of(
                createKey(SECRET_KEY), //Access Token의 키가 deprecated된 저장소에 있다면 가져오고 없다면 새로 만든다.
                Keys.secretKeyFor(signatureAlgorithm));//Refresh Token의 키는 다른 저장소에 있지 않아, 새로 만든다.

        secretKeyCollection.insert(makeDBObject(newJwtKeys));
        return newJwtKeys;
    }

    private SecretKey createKey(String keyFieldName) {
        Optional<String> deprecatedKey = authenticationMapper.getDeprecatedJWTKey(keyFieldName);
        if(!deprecatedKey.isPresent()) {
            return Keys.secretKeyFor(signatureAlgorithm);
        }
        return decode(deprecatedKey.get());
    }

    private boolean needAnyKeyUpdate(DBObject jwtKeysInDB) {
        return needKeyCreate(jwtKeysInDB, ACCESS_KEY_FIELD_NAME) || needKeyCreate(jwtKeysInDB, REFRESH_KEY_FIELD_NAME);
    }

    private boolean needKeyCreate(DBObject jwtKeysInDB, String fieldName) {
        if (!(jwtKeysInDB.containsField(fieldName))) {
            return true;
        }

        return !StringUtils.isEmpty(jwtKeysInDB.get(fieldName));
    }

    private JWTKeys updateKeys(DBCollection secretKeyCollection, DBObject jwtKeysInDB) {
        JWTKeys updatedKeys = JWTKeys.of(
                updateKey(jwtKeysInDB, ACCESS_KEY_FIELD_NAME),
                updateKey(jwtKeysInDB, REFRESH_KEY_FIELD_NAME)
        );
        secretKeyCollection.update(new BasicDBObject(), makeDBObject(updatedKeys), true, false);

        return updatedKeys;
    }

    private SecretKey updateKey(DBObject jwtKeysInDB, String keyFieldName) {
        if (needKeyCreate(jwtKeysInDB, keyFieldName)) {
            return createKey(keyFieldName);
        }

        return decode((String) jwtKeysInDB.get(keyFieldName));
    }

    private JWTKeys getKeys(DBObject jwtKeysInDB) {
        String accessKey = (String) jwtKeysInDB.get(ACCESS_KEY_FIELD_NAME);
        String refreshKey = (String) jwtKeysInDB.get(REFRESH_KEY_FIELD_NAME);

        return JWTKeys.of(decode(accessKey), decode(refreshKey));
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

    private DBObject makeDBObject(JWTKeys jwtKeys) {
        return BasicDBObjectBuilder.start()
                .add(ACCESS_KEY_FIELD_NAME, encode(jwtKeys.getAccessKey()))
                .add(REFRESH_KEY_FIELD_NAME, encode(jwtKeys.getRefreshKey()))
                .get();
    }

}
