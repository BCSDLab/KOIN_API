package koreatech.in.dto.shop.normal.response;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
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
        private List<ShopCategory> shop_categories;
        private List<MenuCategory> menu_categories;
        private List<Menu> menus;

        public void decideWhetherSingleOrNotOfMenus() {
            this.menus.forEach(Menu::decideWhetherSingleOrNot);
        }

        @Getter
        private static final class Open {
            private DayOfWeek day_of_week;
            private Boolean closed;
            private String open_time;
            private String close_time;
        }

        @Getter
        private static final class ShopCategory {
            private Integer id;
            private String name;
        }

        @Getter
        private static final class MenuCategory {
            private Integer id;
            private String name;
        }

        @Getter
        private static final class Menu {
            private Integer id;
            private String name;
            private Boolean is_hidden;
            private Boolean is_single;
            private Integer single_price;
            private List<OptionPrice> option_prices;
            private String description;
            private List<Integer> category_ids;
            private List<String> image_urls;

            public void decideWhetherSingleOrNot() {
                if (this.option_prices.size() == 1 && this.option_prices.get(0).getOption() == null) {
                    this.is_single = true;
                    this.single_price = this.option_prices.get(0).getPrice();
                    this.option_prices = null;
                } else {
                    this.is_single = false;
                }
            }

            @Getter
            private static final class OptionPrice {
                private String option;
                private Integer price;
            }
        }
    }
}
