package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopMenuCategoryMap {
    private Integer id;
    private Integer shop_menu_id;
    private Integer shop_menu_category_id;
    private Date created_at;
    private Date updated_at;

    public static ShopMenuCategoryMap of(Integer shopMenuId, Integer shopMenuCategoryId) {
        return new ShopMenuCategoryMap(shopMenuId, shopMenuCategoryId);
    }

    private ShopMenuCategoryMap(Integer shopMenuId, Integer shopMenuCategoryId) {
        this.shop_menu_id = shopMenuId;
        this.shop_menu_category_id = shopMenuCategoryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ShopMenuCategoryMap)) {
            return false;
        }

        return Objects.equals(shop_menu_id, ((ShopMenuCategoryMap) obj).getShop_menu_id())
                && Objects.equals(shop_menu_category_id, ((ShopMenuCategoryMap) obj).getShop_menu_category_id());
    }
}
