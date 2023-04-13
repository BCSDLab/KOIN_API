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

@Getter @Setter
@ToString
@NoArgsConstructor
public class Owner extends User {
    private Integer user_id;
    private String company_registration_number;
    private String company_registration_certificate_image_url;
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

    public boolean needToUpdate(OwnerUpdateRequest request) {
        return !Objects.equals(this.company_registration_number,request.getCompany_registration_number())
                || !Objects.equals(this.company_registration_certificate_image_url, request.getCompany_registration_certificate_image_url())
                || !Objects.equals(this.grant_shop, request.getGrant_shop())
                || !Objects.equals(this.grant_event, request.getGrant_event());
    }

    public void updateAll(OwnerUpdateRequest request) {
        this.company_registration_number=request.getCompany_registration_number();
        this.company_registration_certificate_image_url=request.getCompany_registration_certificate_image_url();
        this.grant_shop=request.getGrant_shop();
        this.grant_event=request.getGrant_event();
    }

    public void enrichAuthComplete() {
        setUser_type(UserType.OWNER);
        setIs_authed(false);
    }
}
