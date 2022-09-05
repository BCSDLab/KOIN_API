package koreatech.in.domain.Shop;

import lombok.Getter;

import java.util.Date;

@Getter
public class ShopMenuCategoryMap {
    private Integer id;
    private Integer shop_menu_id;
    private Integer shop_menu_category_id;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public void setMapping(Integer shop_menu_id, Integer shop_menu_category_id) {
        this.shop_menu_id = shop_menu_id;
        this.shop_menu_category_id = shop_menu_category_id;
    }
}
