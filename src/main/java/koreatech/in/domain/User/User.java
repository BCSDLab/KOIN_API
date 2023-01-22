package koreatech.in.domain.User;

import koreatech.in.domain.Authority;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import koreatech.in.util.DateUtil;
import koreatech.in.util.SHA256Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public abstract class User {
    protected Integer id;
    protected String account;
    protected String password;
    protected String nickname;
    protected String name;
    protected String phone_number;
    protected UserType user_type;
    protected String email;
    protected Integer gender;
    protected Boolean is_authed;
    protected Date last_logged_at;
    protected String profile_image_url;
    protected String auth_token;
    protected Date auth_expired_at;
    protected String reset_token;
    protected Date reset_expired_at;
    protected Authority authority;
    protected Boolean is_deleted;
    protected Date created_at;
    protected Date updated_at;

    protected User(String account, String password, String nickname, String name, String phoneNumber, String email, Integer gender, UserType userType) {
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone_number = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.user_type = userType;
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
        if(user.phone_number != null) {
            this.phone_number = user.phone_number;
        }
        if(user.email != null) {
            this.email = user.email;
        }
        if (user.is_authed != null) {
            this.is_authed = user.is_authed;
        }
        if (user.profile_image_url != null) {
            this.profile_image_url = user.profile_image_url;
        }
        if (user.auth_token != null) {
            this.auth_token = user.auth_token;
        }
        if (user.auth_expired_at != null) {
            this.auth_expired_at = user.auth_expired_at;
        }
        if (user.reset_token != null) {
            this.reset_token = user.reset_token;
        }
        if (user.reset_expired_at != null) {
            this.reset_expired_at = user.reset_expired_at;
        }
        if (user.last_logged_at != null) {
            this.last_logged_at = user.last_logged_at;
        }
    }

    public boolean isStudent() {
        return this.getClass().equals(Student.class);
    }

    public boolean isOwner() {
        return this.getClass().equals(Owner.class);
    }

    public boolean hasAuthority() {
        return this.authority != null;
    }

    public boolean isEmailAuthenticationCompleted() {
        return this.is_authed.equals(true);
    }

    public boolean isAuthTokenExpired() {
        return auth_expired_at != null && (auth_expired_at.getTime() < (new Date()).getTime());
    }

    public boolean isAwaitingEmailAuthentication() {
        return !isEmailAuthenticationCompleted()
                && this.auth_token != null
                && this.auth_expired_at != null
                && !isAuthTokenExpired();
    }

    public void changeEmailAuthenticationStatusToComplete() {
        this.is_authed = true;
    }

    public void changeAuthTokenAndExpiredAt(String authToken, Date authExpiredAt){
        this.auth_token = authToken;
        this.auth_expired_at = authExpiredAt;
    }

    public void generateDataForFindPassword() {
        this.reset_expired_at = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        this.reset_token = SHA256Util.getEncrypt(this.account, this.reset_expired_at.toString());
    }

    public void changePassword(String password){
        this.password = password;
    }

    public boolean isUserAuthed() { return is_authed == null ? false : is_authed; }

    public boolean equals(User user){
        return user.id != null && this.id != null && this.id.equals(user.id);
    }
}
