package koreatech.in.domain.Auth;


import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class JWTKeys {
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    private JWTKeys(SecretKey accessKey, SecretKey refreshKey) {
        this.accessKey = accessKey;
        this.refreshKey = refreshKey;
    }

    public String getEncodedAccessKey() {
        return encode(getAccessKey());
    }

    public String getEncodedRefreshKey() {
        return encode(getRefreshKey());
    }

    public static JWTKeys of(String encodedAccessKey, String encodedRefreshKey) {
        return new JWTKeys(decode(encodedAccessKey), decode(encodedRefreshKey));
    }

    public static String encode(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(rawData);
    }

    public static SecretKey decode(String encodedKey) {
        if (!StringUtils.hasText(encodedKey)) {
            return null;
        }
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, signatureAlgorithm.getJcaName());
    }

}
