package koreatech.in.controller.v2.dto.shop.response;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ResponseShopMenuForOwnerDTO {
    private Integer id;
    private String name;
    private Boolean is_single;
    private Integer single_price;
    private List<Map<String, Object>> option_prices;
    private List<String> categories;
    private String description;
    private List<String> image_urls;

    public ResponseShopMenuForOwnerDTO discernSingleOrOption() {
        // 단일메뉴 여부 판단 후 필드 세팅
        if ((this.option_prices.size() == 1) &&
                ((this.option_prices.get(0).get("option")) == null)) {
            this.is_single = true;
            this.single_price = ((Long)this.option_prices.get(0).get("price")).intValue();
            this.option_prices = null;
        } else {
            this.is_single = false;
        }

        return this;
    }
}
