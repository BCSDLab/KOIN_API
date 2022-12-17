package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ShopMenuCategoryMap {
    private Integer id;
    private Integer shop_menu_id;
    private Integer shop_menu_category_id;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public static ShopMenuCategoryMap create(Integer shopMenuId, Integer shopMenuCategoryId) {
        return new ShopMenuCategoryMap(shopMenuId, shopMenuCategoryId);
    }

    private ShopMenuCategoryMap(Integer shopMenuId, Integer shopMenuCategoryId) {
        this.shop_menu_id = shopMenuId;
        this.shop_menu_category_id = shopMenuCategoryId;
    }
}
