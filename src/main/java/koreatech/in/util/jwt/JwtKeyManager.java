package koreatech.in.util.jwt;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
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
    private static final String ACCESS_KEY_FIELD_NAME = "access_key";
    private static final String REFRESH_KEY_FIELD_NAME = "refresh_key";
    private static final String SECRET_KEY_COLLECTION = "secret_key";

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
    public void KeySetterForV2() {
        DBCollection secretKeyCollection = mongoTemplate.getCollection(SECRET_KEY_COLLECTION);

        this.jwtKeys = enrichKeysAndSetDB(secretKeyCollection);
    }

    private static String encode(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(rawData);
    }

    private static SecretKey decode(String encodedKey) {
        if (!StringUtils.hasText(encodedKey)) {
            return null;
        }
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, signatureAlgorithm.getJcaName());
    }

    private JWTKeys enrichKeysAndSetDB(DBCollection secretKeyCollection) {
        Optional<DBObject> jwtKeysInDBOptional = Optional.ofNullable(secretKeyCollection.findOne());

        if (!jwtKeysInDBOptional.isPresent()) {
            return createAndInsertDB(secretKeyCollection);
        }

        DBObject jwtKeysInDB = jwtKeysInDBOptional.get();

        if(!needAnyKeyUpdate(jwtKeysInDB)) {
            return maintainKeysOnly(jwtKeysInDB);
        }
        return upsertJWTKeys(secretKeyCollection, jwtKeysInDB);
    }

    private JWTKeys upsertJWTKeys(DBCollection secretKeyCollection, DBObject jwtKeysInDB) {
        JWTKeys enrichedJwtKeys = JWTKeys.of(
                enrichKeyFor(jwtKeysInDB, ACCESS_KEY_FIELD_NAME),
                enrichKeyFor(jwtKeysInDB, REFRESH_KEY_FIELD_NAME)
        );

        secretKeyCollection.update(new BasicDBObject(), toDBObjectWithEncode(enrichedJwtKeys), true, false);

        return enrichedJwtKeys;
    }

    private JWTKeys maintainKeysOnly(DBObject jwtKeysInDB) {
        String accessKey = getKeyFor(jwtKeysInDB, ACCESS_KEY_FIELD_NAME);
        String refreshKey = getKeyFor(jwtKeysInDB, REFRESH_KEY_FIELD_NAME);

        return JWTKeys.of(decode(accessKey), decode(refreshKey));
    }

    private JWTKeys createAndInsertDB(DBCollection secretKeyCollection) {
        JWTKeys newJwtKeys = createKeys();
        secretKeyCollection.insert(toDBObjectWithEncode(newJwtKeys));
        return newJwtKeys;
    }

    private SecretKey enrichKeyFor(DBObject jwtKeysInDB, String keyFieldName) {
        if(needKeyCreate(jwtKeysInDB, keyFieldName)) {
            return createKey(keyFieldName);
        }

        return decode(getKeyFor(jwtKeysInDB, keyFieldName));
    }

    private boolean needAnyKeyUpdate(DBObject jwtKeysInDB) {
        return needKeyCreate(jwtKeysInDB, ACCESS_KEY_FIELD_NAME) || needKeyCreate(jwtKeysInDB, REFRESH_KEY_FIELD_NAME);
    }

    private Boolean needKeyCreate(DBObject jwtKeysInDB, String fieldName) {
        if (!(jwtKeysInDB.containsField(fieldName))) {
            return true;
        }
        Object fieldKey = jwtKeysInDB.get(fieldName);

        if (!(fieldKey instanceof String)) {
            return true;
        }
        return ((String) fieldKey).isEmpty();
    }

    private String getKeyFor(DBObject jwtKeysInDB, String fieldName) {
        return (String) jwtKeysInDB.get(fieldName);
    }

    private JWTKeys createKeys() {
        return JWTKeys.of(createKey(ACCESS_KEY_FIELD_NAME),
                createKey(REFRESH_KEY_FIELD_NAME));
    }

    private SecretKey createKey(String keyFieldName) {
        Optional<String> keyFromRedis = getKeyFor(keyFieldName);

        if(keyFromRedis.isPresent()) {
            return decode(keyFromRedis.get());
        }
        return createKey();
    }

    private Optional<String> getKeyFor(String keyFieldName) {
        if(!ACCESS_KEY_FIELD_NAME.equals(keyFieldName)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(redisAuthenticationMapper.getKey());
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private SecretKey createKey() {
        return Keys.secretKeyFor(signatureAlgorithm);
    }

    private DBObject toDBObjectWithEncode(JWTKeys jwtKeys) {
        JsonObject encodedKeysJson = new JsonObject();

        encodedKeysJson.addProperty(ACCESS_KEY_FIELD_NAME, encode(jwtKeys.getAccessKey()));
        encodedKeysJson.addProperty(REFRESH_KEY_FIELD_NAME, encode(jwtKeys.getRefreshKey()));

        return (DBObject) JSON.parse(encodedKeysJson.toString());
    }
}
