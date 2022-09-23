package koreatech.in.domain.Shop;

import lombok.Getter;

import java.util.Date;

@Getter
public class ShopImage {
    private Integer id;
    private Integer shop_id;
    private String image_url;
    private Date is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopImage(Integer shop_id, String image_url) {
        this.shop_id = shop_id;
        this.image_url = image_url;
    }
}
