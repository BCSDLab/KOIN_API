package koreatech.in.util.jwt;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public abstract class AccessJwtGenerator<T> extends AbstractJwtGenerator<T> {

    @Override
    protected final SecretKey getKey() {
        return super.jwtKeyManager.getAccessKey();
    }

}
