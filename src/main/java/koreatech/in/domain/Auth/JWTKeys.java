package koreatech.in.domain.Auth;


import javax.crypto.SecretKey;
import lombok.Getter;

@Getter
public class JWTKeys {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    private JWTKeys(SecretKey accessKey, SecretKey refreshKey) {
        this.accessKey = accessKey;
        this.refreshKey = refreshKey;
    }

    public static JWTKeys of(SecretKey accessKey, SecretKey refreshKey) {
        return new JWTKeys(accessKey, refreshKey);
    }

}
