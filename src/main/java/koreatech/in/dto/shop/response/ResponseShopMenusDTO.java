package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.ShopMenu;
import koreatech.in.dto.shop.response.inner.ShopMenuCategory;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponseShopMenusDTO {
    private Integer shop_id;
    private String shop_name;
    private List<ShopMenuCategory> menu_categories;
    private List<ShopMenu> menus;

    public ResponseShopMenusDTO decideOptionalOfMenus() {
        this.menus.forEach(ShopMenu::decideSingleOrOption);
        return this;
    }
}
