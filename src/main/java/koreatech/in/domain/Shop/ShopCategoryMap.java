package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ShopCategoryMap {
    private Integer id;
    private Integer shop_id;
    private Integer shop_category_id;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public static ShopCategoryMap of(Integer shopId, Integer shopCategoryId) {
        return new ShopCategoryMap(shopId, shopCategoryId);
    }

    private ShopCategoryMap(Integer shopId, Integer shopCategoryId) {
        this.shop_id = shopId;
        this.shop_category_id = shopCategoryId;
    }
}
