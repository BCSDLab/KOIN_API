package koreatech.in.util.jwt;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class AccessJwtGenerator<T> extends AbstractJwtGenerator<T> {

    @Override
    protected long getTokenValidHour() {
        throw new RuntimeException("추상 클래스의 메서드를 호출함.");
    }

    @Override
    protected final SecretKey getKey() {
        return super.jwtKeyManager.getAccessKey();
    }

    @Override
    protected T toData(String subject) throws IllegalStateException {
        throw new RuntimeException("추상 클래스의 메서드를 호출함.");
    }

    @Override
    public String generateToken(T data) {
        throw new RuntimeException("추상 클래스의 메서드를 호출함.");
    }
}
