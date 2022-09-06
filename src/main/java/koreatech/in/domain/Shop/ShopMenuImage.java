package koreatech.in.domain.Shop;

import lombok.Getter;

import java.util.Date;

@Getter
public class ShopMenuImage {
    private Integer id;
    private Integer shop_menu_id;
    private String image_url;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuImage(Integer shop_menu_id, String image_url) {
        this.shop_menu_id = shop_menu_id;
        this.image_url = image_url;
    }
}
