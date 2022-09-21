package koreatech.in.controller.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import koreatech.in.controller.user.dto.UserResponse;
import koreatech.in.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoginResponse {
    private UserResponse user;
    private int ttl;
    private String token;

    public LoginResponse(User user, int ttl, String token) {
        this.user = new UserResponse(user);
        this.ttl = ttl;
        this.token = token;
    }
}
