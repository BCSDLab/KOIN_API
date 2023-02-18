package koreatech.in.dto.normal.user.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserResponse {
    private Integer id;
    private String email;
    private String nickname;
    private String name;
    private String phoneNumber;
    private Integer gender;
    private Boolean isAuth;
    private UserType userType;
    private Boolean isDeleted;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.phoneNumber = user.getPhone_number();
        this.gender = user.getGender();
        this.isAuth = user.getIs_authed();
        this.userType = user.getUser_type();
        this.isDeleted = user.getIs_deleted();
    }
}
