package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopMenuProfile {
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
    public static class OptionPrice {
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

    public boolean isHidden() {
        if (this.is_hidden == null) {
            return false;
        }

        return this.is_hidden.equals(true);
    }
}
