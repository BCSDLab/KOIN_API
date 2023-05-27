package koreatech.in.domain.Auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResult {
    private final String accessToken;
    private final String refreshToken;
    private final String userType;
}
