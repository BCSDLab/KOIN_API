package koreatech.in.dto.normal.user.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class LoginResponse {
    private final String token;
}

