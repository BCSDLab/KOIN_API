package koreatech.in.dto.shop.admin.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 *   특정 상점의 모든 메뉴를 응답
 */

@Getter @Builder
public class AllMenusOfShopResponse {
    private Integer count;
    private List<Menu> menus;

    @Getter
    public static final class Menu {
        private Integer id;
        private String name;
        private Boolean is_hidden;
        private Boolean is_single;
        private Integer single_price;
        private List<OptionPrice> option_prices;
        private List<Integer> category_ids;

        @Getter
        private static final class OptionPrice {
            private String option;
            private Integer price;
        }

        public void decideWhetherSingleOrNot() {
            if (this.option_prices.size() == 1 && this.option_prices.get(0).getOption() == null) {
                this.is_single = true;
                this.single_price = this.option_prices.get(0).getPrice();
                this.option_prices = null;
            } else {
                this.is_single = false;
            }
        }
    }

    public void decideWhetherSingleOrNotOfMenus() {
        this.menus.forEach(Menu::decideWhetherSingleOrNot);
    }
}
