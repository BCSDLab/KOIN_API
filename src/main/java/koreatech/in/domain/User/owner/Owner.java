package koreatech.in.domain.User.owner;

import java.util.List;

import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserType;
import koreatech.in.dto.global.AttachmentUrlRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
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
        if(owner.company_registration_number!=null){
            this.company_registration_number=owner.company_registration_number;
        }
        if(owner.grant_shop!=null){
            this.grant_shop=owner.grant_shop;
        }
        if(owner.grant_event!=null){
            this.grant_event=owner.grant_event;
        }
    }

    public void enrichAuthComplete() {
        setUser_type(UserType.OWNER);
        setIs_authed(false);
    }

    public boolean hasRegistrationInformation() {
        return this.company_registration_number != null;
    }

    public static boolean hasRegistrationInformation(OwnerRegisterRequest request) {
        return hasCompanyNumber(request) && hasAttachmentUrls(request);
    }

    public static boolean isInsufficientRegisterRequest(OwnerRegisterRequest request) {
        return hasCompanyNumber(request) ^ hasAttachmentUrls(request);
    }

    public static boolean hasCompanyNumber(OwnerRegisterRequest request) {
        return request.getCompanyNumber() != null;
    }

    public static boolean hasAttachmentUrls(OwnerRegisterRequest request) {
        List<AttachmentUrlRequest> requestAttachments = request.getAttachmentUrls();
        return request.getAttachmentUrls() != null && !requestAttachments.isEmpty();
    }
}
