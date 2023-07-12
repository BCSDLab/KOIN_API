package koreatech.in.repository;

import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationMapper {
    void setRefreshToken(String refreshToken, Integer userId);

    String getRefreshToken(Integer userId) throws IOException;

    void deleteRefreshToken(Integer userId);
    //TODO 사장님 인증 + 메모리DB 부분도 여기로 추출할 것

    Optional<String> getDeprecatedJWTKey(String keyName);
}
