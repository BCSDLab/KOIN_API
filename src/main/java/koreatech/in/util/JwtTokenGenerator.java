package koreatech.in.util;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import koreatech.in.domain.Auth.JWTKeys;
import koreatech.in.exception.BaseException;
import koreatech.in.repository.AuthenticationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

public class JwtTokenGenerator {
    private static final String ACCESS_KEY_FIELD_NAME = "access_key";
    private static final String REFRESH_KEY_FIELD_NAME = "refresh_key";
    private static final String SECRET_KEY_COLLECTION = "secret_key";

    private static final long REFRESH_TOKEN_VALID_DAYS = 14;
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 72;
    private static final int ACCESS_TOKEN_VALID_HOUR = 2;

    @Autowired
    private AuthenticationMapper redisAuthenticationMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${redis.key.login_prefix}")
    private String redisLoginTokenKeyPrefix;

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    private JWTKeys jwtKeys;

    /*

    private SecretKey accessKey;

    public SecretKey getAccessKey() {
        return accessKey;
    }

    @PostConstruct
    public void keySetter() throws IOException {
        try {
            accessKey = convertStringToSecretKey(stringRedisUtilStr.getDataAsString("secretKey"));
            if (accessKey == null) {
                accessKey = Keys.secretKeyFor(signatureAlgorithm);
                stringRedisUtilStr.setDataAsString("secretKey", convertSecretKeyToString(accessKey));
            }
        } catch (IOException | IllegalArgumentException e) {
            accessKey = Keys.secretKeyFor(signatureAlgorithm);
            stringRedisUtilStr.setDataAsString("secretKey", convertSecretKeyToString(accessKey));
        }
    }

    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(rawData);
    }

    public static SecretKey convertStringToSecretKey(String encodedKey) {
        if (!StringUtils.hasText(encodedKey)) {
            return null;
        }
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, signatureAlgorithm.getJcaName());
    }

    */

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

    public String generateAccessToken(int userId) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), PREV_ACCESS_TOKEN_VALID_HOUR);
        return Jwts.builder().setSubject(String.valueOf(userId)).setExpiration(exp).signWith(getAccessKey()).compact();
    }

    public String generateRefreshToken(Integer userId) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), (int) TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS));
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(exp)
                .signWith(getRefreshKey())
                .compact();
    }

    public int me(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(getAccessKey())
                    .parseClaimsJws(token)
                    .getBody();

            return Integer.parseInt(body.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        throw new BaseException(BAD_ACCESS);
    }

    public int getIdFromRefreshToken(String refreshToken) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(getRefreshKey())
                    .parseClaimsJws(refreshToken)
                    .getBody();

            int userId = Integer.parseInt(body.getSubject());

            String redisToken = redisAuthenticationMapper.getRefreshToken(userId);
            if (refreshToken.equals(redisToken)) {
                return userId;
            }
        } catch (JwtException | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        throw new BaseException(BAD_ACCESS);
    }

    public Boolean isExpired(String token) {
        try {
            Jwts.parser().setSigningKey(getAccessKey()).parseClaimsJws(token);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException e) {
            return true;
        }
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
        Optional<String> keyFromRedis = getKeyFromRedis(keyFieldName);

        if(keyFromRedis.isPresent()) {
            return decode(keyFromRedis.get());
        }
        return createKey();
    }

    private Optional<String> getKeyFromRedis(String keyFieldName) {
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
    //TODO 키 관리 역할과 토큰 생성 역할 분리하기. (PonstConstructor에서 JwtKeyMaintainer? 객체에서 jwtkey 가져오게 하기.
}
