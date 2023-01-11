package koreatech.in.domain.User.owner;

import koreatech.in.domain.User.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class Owner extends User {

    private String companyRegistrationNumber;
    private String companyRegistrationCertificateImageURL;
    // Todo grant 들 별도의 Grant embedded 객체로 매핑
    private Boolean isGrantShop;
    private Boolean isGrantEvent;

    @Override
    public String toString() {
        return "Owner{" +
                "id=" + id +
                ", portal_account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", is_authed=" + isAuthed +
                ", auth_token='" + authToken + '\'' +
                ", auth_expired_at=" + authExpiredAt +
                ", reset_token='" + resetToken + '\'' +
                ", reset_expired_at=" + resetExpiredAt +
                ", last_logged_at='" + lastLoggedAt + '\'' +
                ", profile_image_url='" + profileImageUrl + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void update(Owner owner) {
        super.update(owner);
        if (owner.email != null) {
            this.email = owner.email;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
