package koreatech.in.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.UnauthorizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

import static koreatech.in.exception.ExceptionInformation.*;

public class JwtTokenGenerator {
    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Value("${redis.key.login_prefix}")
    private String redisLoginTokenKeyPrefix;

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private SecretKey key;

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

    @PostConstruct
    public void keySetter() throws IOException {
        try {
            key = convertStringToSecretKey(stringRedisUtilStr.getDataAsString("secretKey"));
            if (key == null) {
                key = Keys.secretKeyFor(signatureAlgorithm);
                stringRedisUtilStr.setDataAsString("secretKey", convertSecretKeyToString(key));
            }
        } catch (IOException | IllegalArgumentException e) {
            key = Keys.secretKeyFor(signatureAlgorithm);
            stringRedisUtilStr.setDataAsString("secretKey", convertSecretKeyToString(key));
        }
    }

    public String generate(int id) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), 72);
        return Jwts.builder().setSubject(String.valueOf(id)).setExpiration(exp).signWith(this.key).compact();
    }

    public int me(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(this.key)
                    .parseClaimsJws(token)
                    .getBody();

            int userId = Integer.parseInt(body.getSubject());

            String redisToken = stringRedisUtilStr.getDataAsString(redisLoginTokenKeyPrefix + userId);
            if (!token.equals(redisToken)) {
                throw new BaseException(ACCESS_TOKEN_CHANGED);
            }

            return userId;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException | IOException e) {
            throw new BaseException(ACCESS_TOKEN_EXPIRED);
        }
    }

    public Boolean isExpired(String token) {
        try {
            Jwts.parser().setSigningKey(this.key).parseClaimsJws(token);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
