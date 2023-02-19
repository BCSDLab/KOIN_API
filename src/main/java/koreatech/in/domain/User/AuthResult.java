package koreatech.in.domain.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class AuthResult {

    private final Boolean result;
    private final String message;


    public static AuthResult USER_NOT_FOUND = AuthResult.of(
            false, "토큰에 해당하는 사용자를 찾을 수 없습니다.");
    public static AuthResult ALREADY_AUTHENTICATION = AuthResult.of(
            false, "이미 인증된 사용자입니다.");
    public static AuthResult TOKEN_EXPIRED = AuthResult.of(
            false, "이미 만료된 토큰입니다.");
    public static AuthResult SUCCESS = AuthResult.of(
            true , null);

    public static AuthResult from(User user) {
        if(user == null) {
            return USER_NOT_FOUND;
        }
        if(user.isEmailAuthenticationCompleted()) {
            return ALREADY_AUTHENTICATION;
        }
        if(user.isAuthTokenExpired()) {
            return TOKEN_EXPIRED;
        }
        return SUCCESS;
    }

    public Boolean isSuccess() {
        return result;
    }

}
