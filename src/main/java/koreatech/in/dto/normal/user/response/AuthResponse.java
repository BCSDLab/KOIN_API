package koreatech.in.dto.normal.user.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthResponse {
    private final boolean isSuccess;
    private final String errorMessage;
}

