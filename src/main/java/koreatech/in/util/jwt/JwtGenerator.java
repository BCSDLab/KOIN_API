package koreatech.in.util.jwt;

public interface JwtGenerator<T> {

    String generateToken(T data);

    T getFromToken(String token);

    Boolean isExpired(String token);
}
