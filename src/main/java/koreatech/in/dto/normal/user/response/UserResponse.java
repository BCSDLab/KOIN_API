package koreatech.in.dto.normal.user.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import koreatech.in.domain.User.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class UserResponse {
    private String email;
    private String nickname;
    private String name;
    private String phoneNumber;
    private Integer gender;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.phoneNumber = user.getPhone_number();
        this.gender = user.getGender();
    }
}
