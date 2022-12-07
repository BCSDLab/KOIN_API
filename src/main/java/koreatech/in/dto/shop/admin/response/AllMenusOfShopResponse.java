package koreatech.in.dto.shop.admin.response;

import koreatech.in.dto.shop.admin.response.inner.ShopMenu;
import koreatech.in.dto.shop.admin.response.inner.ShopMenuCategory;
import lombok.Getter;

import java.util.List;

/**
 *   특정 상점의 모든 메뉴를 응답
 */

@Getter
public class AllMenusOfShopResponse {
    private Integer shop_id;
    private String shop_name;
    private List<ShopMenuCategory> menu_categories;
    private List<ShopMenu> menus;

    public AllMenusOfShopResponse decideOptionalOfMenus() {
        this.menus.forEach(ShopMenu::decideWhetherSingleOrNot);
        return this;
    }
}
