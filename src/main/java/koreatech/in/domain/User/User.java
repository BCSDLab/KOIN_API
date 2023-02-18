package koreatech.in.domain.User;

import static koreatech.in.exception.ExceptionInformation.IMPOSSIBLE_UNDELETE_USER_BECAUSE_SAME_EMAIL_EXIST;
import static koreatech.in.exception.ExceptionInformation.USER_HAS_NOT_WITHDRAWN;
import static koreatech.in.exception.ExceptionInformation.USER_HAS_WITHDRAWN;

import java.util.Date;
import koreatech.in.domain.Authority;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import koreatech.in.exception.BaseException;
import koreatech.in.util.DateUtil;
import koreatech.in.util.SHA256Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class User {
    protected Integer id;
    protected String email;
    protected String password;
    protected String nickname;
    protected String name;
    protected String phone_number;
    protected UserType user_type;
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

    protected User(String email, String password, String nickname, String name, String phoneNumber, Integer gender, UserType userType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone_number = phoneNumber;
        this.gender = gender;
        this.user_type = userType;
        this.is_authed = false;
    }

    public void update(User user) {
        if (user.email != null) {
            this.email = user.email;
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

    public boolean hasSameId(Integer id) {
        return this.id.equals(id);
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

    public boolean isResetTokenExpired() {
        return this.reset_expired_at != null && (this.reset_expired_at.getTime() < (new Date()).getTime());
    }

    public boolean isAwaitingToFindPassword() {
        return this.reset_token != null
                && this.reset_expired_at != null
                && !isResetTokenExpired();
    }

    public boolean isWithdrawn() {
        return this.is_deleted;
    }

    public void changeEmailAuthenticationStatusToComplete() {
        this.is_authed = true;
    }

    public void changeAuthTokenAndExpiredAt(String authToken, Date authExpiredAt){
        this.auth_token = authToken;
        this.auth_expired_at = authExpiredAt;
    }

    public void generateResetTokenForFindPassword() {
        this.reset_expired_at = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        this.reset_token = SHA256Util.getEncrypt(this.email, this.reset_expired_at.toString());
    }

    public void changeToNewPassword(String password) {
        this.password = password;
        this.reset_expired_at = new Date();
    }

    public void checkPossibilityOfDeletion() {
        if (isWithdrawn()) {
            throw new BaseException(USER_HAS_WITHDRAWN);
        }
    }

    public void checkPossibilityOfUndeletion(User undeletedAndSameEmailUser) {
        if (!isWithdrawn()) {
            throw new BaseException(USER_HAS_NOT_WITHDRAWN);
        }
        if (undeletedAndSameEmailUser != null) {
            throw new BaseException(IMPOSSIBLE_UNDELETE_USER_BECAUSE_SAME_EMAIL_EXIST);
        }
    }

    public void changePassword(String password){
        this.password = password;
    }

    public boolean isUserAuthed() { return is_authed == null ? false : is_authed; }

    public boolean equals(User user){
        return user.id != null && this.id != null && this.id.equals(user.id);
    }

    public void enrichUserType() {
        setUser_type(UserType.mappingFor(this));
    }

    public String getText() {
        if(getUser_type() == null) {
            enrichUserType();
        }
        return getUser_type().getText();
    }
}
