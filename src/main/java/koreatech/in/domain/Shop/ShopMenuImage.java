package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ShopMenuImage {
    private Integer id;
    private Integer shop_menu_id;
    private String image_url;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuImage(Integer shopMenuId, String imageUrl) {
        this.shop_menu_id = shopMenuId;
        this.image_url = imageUrl;
    }
}
