package koreatech.in.domain.Shop;

import lombok.Getter;

import java.util.Date;

@Getter
public class Image {
    private Integer id;
    private String image_url;
    private Date is_deleted;
    private Date created_at;
    private Date updated_at;

    public Image(String image_url) {
        this.image_url = image_url;
    }
}
