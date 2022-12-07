package koreatech.in.dto.shop.admin.response.inner;

import lombok.Getter;

import java.util.List;

@Getter
public class ShopMenu {
    private Integer id;
    private Integer shop_id;
    private String name;
    private Boolean is_hidden;
    private Boolean is_single;
    private Integer single_price;
    private List<OptionPrice> option_prices;
    private List<Integer> category_ids;
    private String description;
    private List<String> image_urls;

    public boolean hasSameShopId(Integer shopId) {
        if (this.shop_id == null || shopId == null) {
            return false;
        }

        return this.shop_id.equals(shopId);
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
