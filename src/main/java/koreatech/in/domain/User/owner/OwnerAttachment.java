package koreatech.in.domain.User.owner;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OwnerAttachment {

    private Integer id;
    private Integer ownerId;
    private Integer shopId;
    private String fileUrl;
    private Boolean isDeleted;
    private Date updateAt;

    public static OwnerAttachment of(Integer ownerId, String fileUrl) {
        OwnerAttachment ownerAttachment = new OwnerAttachment();
        ownerAttachment.setOwnerId(ownerId);
        ownerAttachment.setFileUrl(fileUrl);

        return ownerAttachment;
    }

}
