package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Getter
public class ShopMenuCategoryMap {
    private Integer id;
    private Integer shop_menu_id;
    private Integer shop_menu_category_id;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuCategoryMap(Integer shop_menu_id, Integer shop_menu_category_id) {
        this.shop_menu_id = shop_menu_id;
        this.shop_menu_category_id = shop_menu_category_id;
    }
}
