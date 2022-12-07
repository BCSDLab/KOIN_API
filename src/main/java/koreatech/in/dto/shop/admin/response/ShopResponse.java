package koreatech.in.dto.shop.admin.response;

import koreatech.in.dto.shop.admin.response.inner.Open;
import koreatech.in.dto.shop.admin.response.inner.ShopMenu;
import koreatech.in.dto.shop.admin.response.inner.ShopMenuCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class ShopResponse {
    private Shop shop;

    @Getter
    public static final class Shop {
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

        public void decideWhetherSingleOrNotOfMenus() {
            this.menus.forEach(ShopMenu::decideWhetherSingleOrNot);
        }
    }
}
