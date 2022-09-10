package koreatech.in.domain.User;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

public class User implements UserDetails {
    @ApiModelProperty(notes = "고유 id", example = "10")
    protected Integer id;
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "아이디는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "아이디 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "학교 계정 아이디", example = "jjw266")
    protected String portal_account;
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "비밀번호는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "비밀번호", example = "a0240120305812krlakdsflsa;1235")
    protected String password;
    @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
    @ApiModelProperty(notes = "닉네임", example = "bbo")
    protected String nickname;
    @ApiModelProperty(notes = "익명 닉네임", example = "익명_1522771686642")
    protected String anonymous_nickname;
    @Size(max = 50, message = "이름은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "이름", example = "정보혁")
    protected String name;
    @Size(max = 50, message = "학번은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "학번", example = "2013136000")
    protected String student_number;
    @ApiModelProperty(notes = "기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부, 고용서비스정책학과", example = "컴퓨터공학부")
    protected String major;
    @ApiModelProperty(notes = "신원(0: 학생, 1: 대학원생, 2: 교수, 3: 교직원, 4: 졸업생, 5: 점주)", example = "0")
    protected Integer identity;
    @ApiModelProperty(notes = "졸업 여부", example = "false")
    protected Boolean is_graduated;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "휴대폰 번호", example = "010-0000-0000")
    protected String phone_number;
    @ApiModelProperty(notes = "성별(남:0, 여:1)", example = "0")
    protected Integer gender;
    @ApiModelProperty(hidden = true)
    protected Boolean is_authed;
    @ApiModelProperty(hidden = true)
    protected String auth_token;
    @ApiModelProperty(hidden = true)
    protected Date auth_expired_at;
    @ApiModelProperty(hidden = true)
    protected String reset_token;
    @ApiModelProperty(hidden = true)
    protected Date reset_expired_at;
    @ApiModelProperty(hidden = true)
    protected String last_logged_at;
    @ApiModelProperty(hidden = true)
    protected String remember_token;
    @ApiModelProperty(notes = "프로필 이미지 링크", example = "https://static.koreatech.in/upload/~~")
    protected String profile_image_url;
    @ApiModelProperty(hidden = true)
    protected Authority authority;

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPortal_account() {
        return portal_account;
    }

    public void setPortal_account(String portal_account) {
        this.portal_account = portal_account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return portal_account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAnonymous_nickname() {
        return anonymous_nickname;
    }

    public void setAnonymous_nickname(String anonymous_nickname) {
        this.anonymous_nickname = anonymous_nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudent_number() {
        return student_number;
    }

    public void setStudent_number(String student_number) {
        this.student_number = student_number;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public Boolean getIs_graduated() {
        return is_graduated;
    }

    public void setIs_graduated(Boolean is_graduated) {
        this.is_graduated = is_graduated;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Boolean getIs_authed() {
        return is_authed;
    }

    public void setIs_authed(Boolean is_authed) {
        this.is_authed = is_authed;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public Date getAuth_expired_at() {
        return auth_expired_at;
    }

    public void setAuth_expired_at(Date auth_expired_at) {
        this.auth_expired_at = auth_expired_at;
    }

    public String getReset_token() {
        return reset_token;
    }

    public void setReset_token(String reset_token) {
        this.reset_token = reset_token;
    }

    public Date getReset_expired_at() {
        return reset_expired_at;
    }

    public void setReset_expired_at(Date reset_expired_at) {
        this.reset_expired_at = reset_expired_at;
    }

    public String getLast_logged_at() {
        return last_logged_at;
    }

    public void setLast_logged_at(String last_logged_at) {
        this.last_logged_at = last_logged_at;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", portal_account='" + portal_account + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", anonymous_nickname='" + anonymous_nickname + '\'' +
                ", name='" + name + '\'' +
                ", student_number='" + student_number + '\'' +
                ", major='" + major + '\'' +
                ", identity=" + identity +
                ", is_graduated=" + is_graduated +
                ", phone_number='" + phone_number + '\'' +
                ", gender=" + gender +
                ", is_authed=" + is_authed +
                ", auth_token='" + auth_token + '\'' +
                ", auth_expired_at=" + auth_expired_at +
                ", reset_token='" + reset_token + '\'' +
                ", reset_expired_at=" + reset_expired_at +
                ", last_logged_at='" + last_logged_at + '\'' +
                ", remember_token='" + remember_token + '\'' +
                ", profile_image_url='" + profile_image_url + '\'' +
                ", authority=" + authority +
                '}';
    }

    public void update(User user) {
        if (user.portal_account != null) {
            this.portal_account = user.portal_account;
        }
        if (user.password != null) {
            this.password = user.password;
        }
        if (user.nickname != null) {
            this.nickname = user.nickname;
        }
        if (user.anonymous_nickname != null) {
            this.anonymous_nickname = user.anonymous_nickname;
        }
        if (user.name != null) {
            this.name = user.name;
        }
        if (user.student_number != null) {
            this.student_number = user.student_number;
        }
        if (user.major != null) {
            this.major = user.major;
        }
        if (user.identity != null) {
            this.identity = user.identity;
        }
        if (user.is_graduated != null) {
            this.is_graduated = user.is_graduated;
        }
        if (user.phone_number != null) {
            this.phone_number = user.phone_number;
        }
        if (user.gender != null) {
            this.gender = user.gender;
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
}
