package koreatech.in.domain.Shop;

import lombok.Getter;

@Getter
public class ShopMenuImage extends Image {
    private Integer shop_menu_id;

    public ShopMenuImage(Integer shop_menu_id, String image_url) {
        super(image_url);
        this.shop_menu_id = shop_menu_id;
    }
}
