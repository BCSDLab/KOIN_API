package koreatech.in.domain.User.owner;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OwnerShopAttachment {

    private Integer id;
    private Integer ownerId;
    private Integer shopId;
    private String url;
    private Boolean isDeleted;
    private Date updateAt;

    public static OwnerShopAttachment of(Integer ownerId, String url) {
        OwnerShopAttachment ownerShopAttachment = new OwnerShopAttachment();
        ownerShopAttachment.setOwnerId(ownerId);
        ownerShopAttachment.setUrl(url);

        return ownerShopAttachment;
    }

}
