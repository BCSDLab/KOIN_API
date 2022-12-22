package koreatech.in.dto.shop.admin.response;

import koreatech.in.domain.Shop.ShopMenuCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class MenuResponse {
    private List<MenuCategory> menu_categories_of_shop;
    @Setter private Menu menu;

    public void setMenuCategoriesOfShopFrom(List<ShopMenuCategory> shopMenuCategories) {
        this.menu_categories_of_shop = shopMenuCategories.stream()
                .map(shopMenuCategory ->
                        MenuCategory.builder()
                                .id(shopMenuCategory.getId())
                                .name(shopMenuCategory.getName())
                                .build()
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Getter @Builder
    public static final class MenuCategory {
        private Integer id;
        private String name;
    }

    @Getter
    public static final class Menu {
        private Integer id;
        private Integer shop_id;
        private String name;
        private Boolean is_hidden;
        private Boolean is_single;
        private Integer single_price;
        private List<OptionPrice> option_prices;
        private String description;
        private List<Integer> category_ids;
        private List<String> image_urls;

        @Getter
        private static final class OptionPrice {
            private String option;
            private Integer price;
        }

        public boolean hasSameShopId(Integer shopId) {
            if (this.shop_id == null || shopId == null) {
                return false;
            }

            return Objects.equals(this.shop_id, shopId);
        }

        public void decideWhetherSingleOrNot() {
            // 단일 메뉴일경우
            if (this.option_prices.size() == 1 && this.option_prices.get(0).getOption() == null) {
                this.is_single = true;
                this.single_price = this.option_prices.get(0).getPrice();
                this.option_prices = null;
            }
            // 단일 메뉴가 아닐 경우
            else {
                this.is_single = false;
            }
        }
    }
}
