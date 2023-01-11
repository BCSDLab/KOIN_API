package koreatech.in.domain.User;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public abstract class User implements UserDetails {
    @ApiModelProperty(notes = "고유 id", example = "10")
    protected Integer id;
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "아이디는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "아이디 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "아이디", example = "jjw266")
    protected String account;
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "비밀번호는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "비밀번호", example = "a0240120305812krlakdsflsa;1235")
    protected String password;
    @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
    @ApiModelProperty(notes = "닉네임", example = "bbo")
    protected String nickname;
    @Size(max = 50, message = "이름은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "이름", example = "정보혁")
    protected String name;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "휴대폰 번호", example = "010-0000-0000")
    protected String phoneNumber;
    @ApiModelProperty(notes = "이메일", example = "abc@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    protected String email;
    @ApiModelProperty(notes = "성별(남:0, 여:1)", example = "0")
    protected Integer gender;
    @ApiModelProperty(hidden = true)
    protected Boolean isAuthed;
    protected String authToken;
    protected Date authExpiredAt;
    protected String resetToken;
    protected Date resetExpiredAt;
    @ApiModelProperty(notes = "프로필 이미지 링크", example = "https://static.koreatech.in/upload/~~")
    protected String profileImageUrl;
    protected Date lastLoggedAt;
    protected Authority authority;
    protected UserType userType;
    protected Boolean isDeleted;
    protected Date createdAt;
    protected Date updatedAt;

    protected User(String account, String password, String nickname, String name, String phoneNumber, String email, Integer gender, UserType userType) {
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", is_authed=" + isAuthed +
                ", auth_token='" + authToken + '\'' +
                ", auth_expired_at=" + authExpiredAt +
                ", reset_token='" + resetToken + '\'' +
                ", reset_expired_at=" + resetExpiredAt +
                ", last_logged_at='" + lastLoggedAt + '\'' +
                ", profile_image_url='" + profileImageUrl + '\'' +
                '}';
    }

    public void update(User user) {
        if (user.account != null) {
            this.account = user.account;
        }
        if (user.password != null) {
            this.password = user.password;
        }
        if (user.nickname != null) {
            this.nickname = user.nickname;
        }
        if (user.name != null) {
            this.name = user.name;
        }
        if (user.gender != null) {
            this.gender = user.gender;
        }
        if(user.phoneNumber != null) {
            this.phoneNumber = user.phoneNumber;
        }
        if(user.email != null) {
            this.email = user.email;
        }
        if (user.isAuthed != null) {
            this.isAuthed = user.isAuthed;
        }
        if (user.profileImageUrl != null) {
            this.profileImageUrl = user.profileImageUrl;
        }
        if (user.authToken != null) {
            this.authToken = user.authToken;
        }
        if (user.authExpiredAt != null) {
            this.authExpiredAt = user.authExpiredAt;
        }
        if (user.resetToken != null) {
            this.resetToken = user.resetToken;
        }
        if (user.resetExpiredAt != null) {
            this.resetExpiredAt = user.resetExpiredAt;
        }
        if (user.lastLoggedAt != null) {
            this.lastLoggedAt = user.lastLoggedAt;
        }
    }

    public void changeAuthTokenAndExpiredAt(String authToken, Date authExpiredAt){
        this.authToken = authToken;
        this.authExpiredAt = authExpiredAt;
    }

    public void changePassword(String password){
        this.password = password;
    }

    public boolean isAwaitingEmailAuthenticate(){
        return !isUserAuthed() && !isAuthTokenExpired();
    }
    public boolean isUserAuthed() { return isAuthed == null ? false : isAuthed; }
    public boolean isAuthTokenExpired(){
        return authExpiredAt == null ? false : authExpiredAt.getTime() - (new Date()).getTime() < 0;
    }

    public boolean equals(User user){
        return user.id != null && this.id != null && this.id.equals(user.id);
    }
}
