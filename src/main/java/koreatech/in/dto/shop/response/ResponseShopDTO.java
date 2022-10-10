package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.ShopCategory;
import koreatech.in.dto.shop.response.inner.ShopMenu;
import koreatech.in.dto.shop.response.inner.ShopMenuCategory;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ResponseShopDTO {
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private String description;
    private Boolean delivery;
    private Integer delivery_price;
    private Boolean pay_card;
    private Boolean pay_bank;
    private List<Map<String, Object>> open;
    private List<String> image_urls;
    private List<ShopCategory> shop_categories;
    private List<ShopMenuCategory> menu_categories;
    private List<ShopMenu> menus;

    public ResponseShopDTO decideOptionalOfMenus() {
        this.menus.forEach(ShopMenu::decideSingleOrOption);
        return this;
    }
}
