package koreatech.in.domain.User.owner;

import java.util.List;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class Owner extends User {
    private Integer user_id;
    private String company_registration_number;
    private List<OwnerAttachment> attachments;
    private List<Shop> shops;

    // Todo grant 들 별도의 Grant embedded 객체로 매핑
    private Boolean grant_shop;
    private Boolean grant_event;

    public boolean hasGrantedShop() {
        return this.grant_shop != null && this.grant_shop;
    }

    public boolean hasGrantedEvent() {
        return this.grant_event != null && this.grant_event;
    }

    public void update(Owner owner) {
        super.update(owner);
        if (owner.email != null) {
            this.email = owner.email;
        }
    }

    public void enrichAuthComplete() {
        setUser_type(UserType.OWNER);
        setIs_authed(false);
    }
}
