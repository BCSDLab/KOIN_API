package koreatech.in.domain.User.owner;

import java.util.List;
import java.util.Objects;

import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserType;
import koreatech.in.dto.admin.user.owner.request.OwnerUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
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

    public boolean needToUpdate(Owner owner) {
        return !Objects.equals(this.company_registration_number, owner.getCompany_registration_number())
                || !Objects.equals(this.grant_shop, owner.getGrant_shop())
                || !Objects.equals(this.grant_event, owner.getGrant_event())
                || !Objects.equals(this.nickname, owner.getNickname())
                || !Objects.equals(this.phone_number, owner.getPhone_number())
                || !Objects.equals(this.name, owner.getName())
                || !Objects.equals(this.email, owner.getEmail())
                || !Objects.equals(this.gender, owner.getGender());
    }

    public void updateAll(Owner owner) {
        this.company_registration_number = owner.getCompany_registration_number();
        this.grant_shop = owner.getGrant_shop();
        this.grant_event = owner.getGrant_event();
        this.nickname = owner.getNickname();
        this.phone_number = owner.getPhone_number();
        this.name = owner.getName();
        this.email = owner.getEmail();
        this.gender = owner.getGender();
    }

    public void enrichAuthComplete() {
        setUser_type(UserType.OWNER);
        setIs_authed(false);
    }
}
