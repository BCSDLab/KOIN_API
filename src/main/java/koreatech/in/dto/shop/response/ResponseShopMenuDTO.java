package koreatech.in.dto.shop.response;

import koreatech.in.domain.Shop.ShopMenuDetail;
import koreatech.in.dto.shop.response.inner.ShopMenuCategory;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class ResponseShopMenuDTO {
    private Integer id;
    private Integer shop_id;
    private String name;
    private Boolean is_hidden;
    private Boolean is_single;
    private Integer single_price;
    private List<Map<String, Object>> option_prices;
    private List<ShopMenuCategory> categories;
    private String description;
    private List<String> image_urls;

    public ResponseShopMenuDTO decideSingleOrOption() {
        if (this.option_prices.size() == 1 && this.option_prices.get(0).get("option") == null) {
            this.is_single = true;
            this.single_price = ((Long) this.option_prices.get(0).get("price")).intValue();
            this.option_prices = null;
        } else {
            this.is_single = false;
        }

        return this;
    }
}
