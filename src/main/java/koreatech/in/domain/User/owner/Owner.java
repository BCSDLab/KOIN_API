package koreatech.in.domain.User.owner;

import java.util.List;
import koreatech.in.domain.User.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class Owner extends User {
    private String company_registration_number;
    private List<Attachment> attachments;
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
}
