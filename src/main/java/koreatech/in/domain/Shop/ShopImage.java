package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ShopImage {
    private Integer id;
    private Integer shop_id;
    private String image_url;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopImage(Integer shopId, String imageUrl) {
        this.shop_id = shopId;
        this.image_url = imageUrl;
    }
}
