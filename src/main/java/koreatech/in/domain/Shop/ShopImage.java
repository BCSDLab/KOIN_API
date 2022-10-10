package koreatech.in.domain.Shop;

import lombok.Getter;

@Getter
public class ShopImage extends Image {
    private Integer shop_id;

    public ShopImage(Integer shop_id, String image_url) {
        super(image_url);
        this.shop_id = shop_id;
    }
}
