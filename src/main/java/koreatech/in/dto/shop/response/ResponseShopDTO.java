package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.Open;
import koreatech.in.dto.shop.response.inner.ShopMenu;
import koreatech.in.dto.shop.response.inner.ShopMenuCategory;
import lombok.Getter;

import java.util.List;

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
    private List<Open> open;
    private List<String> image_urls;
    private List<Integer> shop_category_ids;
    private List<ShopMenuCategory> menu_categories;
    private List<ShopMenu> menus;
    private Boolean is_deleted;

    public ResponseShopDTO decideOptionalOfMenus() {
        this.menus.forEach(ShopMenu::decideSingleOrNot);
        return this;
    }
}
