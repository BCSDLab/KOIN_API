package koreatech.in.domain.User;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Authority;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.Date;

public class Owner extends User {
    @ApiModelProperty(notes = "점주의 이메일", example = "abc@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    public void update(User user) {
        super.update(user);

        if (user instanceof Owner) {
            Owner owner = (Owner) user;
            if (owner.email != null) {
                this.email = owner.email;
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Authority getAuthority() {
        return super.getAuthority();
    }

    @Override
    public void setAuthority(Authority authority) {
        super.setAuthority(authority);
    }

    @Override
    public String getPortal_account() {
        return super.getPortal_account();
    }

    @Override
    public void setPortal_account(String portal_account) {
        super.setPortal_account(portal_account);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public String getNickname() {
        return super.getNickname();
    }

    @Override
    public void setNickname(String nickname) {
        super.setNickname(nickname);
    }

    @Override
    public String getAnonymous_nickname() {
        return super.getAnonymous_nickname();
    }

    @Override
    public void setAnonymous_nickname(String anonymous_nickname) {
        super.setAnonymous_nickname(anonymous_nickname);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getStudent_number() {
        return super.getStudent_number();
    }

    @Override
    public void setStudent_number(String student_number) {
        super.setStudent_number(student_number);
    }

    @Override
    public String getMajor() {
        return super.getMajor();
    }

    @Override
    public void setMajor(String major) {
        super.setMajor(major);
    }

    @Override
    public Integer getIdentity() {
        return super.getIdentity();
    }

    @Override
    public void setIdentity(Integer identity) {
        super.setIdentity(identity);
    }

    @Override
    public Boolean getIs_graduated() {
        return super.getIs_graduated();
    }

    @Override
    public void setIs_graduated(Boolean is_graduated) {
        super.setIs_graduated(is_graduated);
    }

    @Override
    public String getPhone_number() {
        return super.getPhone_number();
    }

    @Override
    public void setPhone_number(String phone_number) {
        super.setPhone_number(phone_number);
    }

    @Override
    public Integer getGender() {
        return super.getGender();
    }

    @Override
    public void setGender(Integer gender) {
        super.setGender(gender);
    }

    @Override
    public Boolean getIs_authed() {
        return super.getIs_authed();
    }

    @Override
    public void setIs_authed(Boolean is_authed) {
        super.setIs_authed(is_authed);
    }

    @Override
    public String getAuth_token() {
        return super.getAuth_token();
    }

    @Override
    public void setAuth_token(String auth_token) {
        super.setAuth_token(auth_token);
    }

    @Override
    public Date getAuth_expired_at() {
        return super.getAuth_expired_at();
    }

    @Override
    public void setAuth_expired_at(Date auth_expired_at) {
        super.setAuth_expired_at(auth_expired_at);
    }

    @Override
    public String getReset_token() {
        return super.getReset_token();
    }

    @Override
    public void setReset_token(String reset_token) {
        super.setReset_token(reset_token);
    }

    @Override
    public Date getReset_expired_at() {
        return super.getReset_expired_at();
    }

    @Override
    public void setReset_expired_at(Date reset_expired_at) {
        super.setReset_expired_at(reset_expired_at);
    }

    @Override
    public String getLast_logged_at() {
        return super.getLast_logged_at();
    }

    @Override
    public void setLast_logged_at(String last_logged_at) {
        super.setLast_logged_at(last_logged_at);
    }

    @Override
    public String getRemember_token() {
        return super.getRemember_token();
    }

    @Override
    public void setRemember_token(String remember_token) {
        super.setRemember_token(remember_token);
    }

    @Override
    public String getProfile_image_url() {
        return super.getProfile_image_url();
    }

    @Override
    public void setProfile_image_url(String profile_image_url) {
        super.setProfile_image_url(profile_image_url);
    }

    @Override
    public String toString() {
        return "Owner{" +
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
                ", email='" + email + '\'' +
                ", authority=" + authority +
                '}';
    }

    public void update(Owner owner) {
        super.update(owner);
        if (owner.email != null) {
            this.email = owner.email;
        }
    }
}
