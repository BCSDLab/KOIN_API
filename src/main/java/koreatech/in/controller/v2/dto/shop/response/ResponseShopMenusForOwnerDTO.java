package koreatech.in.controller.v2.dto.shop.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ResponseShopMenusForOwnerDTO {
    private Integer shop_id;
    private String shop_name;
    private List<String> categories;
    private List<Menu> menus;

    @JsonIgnoreProperties
    @Getter
    private static class Menu {
        Integer id;
        String name;
        Boolean is_hidden;
        List<String> categories;
        Boolean is_single;
        Integer single_price;
        List<Map<String, Object>> option_prices;
        List<String> image_urls;
        String description;
    }

    public ResponseShopMenusForOwnerDTO discernSingleOrOption() {
        this.menus.forEach(menu -> {
            if ((menu.option_prices.size() == 1) &&
                    (menu.option_prices.get(0).get("option") == null)) {
                menu.is_single = true;
                menu.single_price = ((Long)menu.option_prices.get(0).get("price")).intValue();
                menu.option_prices = null;
            } else {
                menu.is_single = false;
            }
        });

        return this;
    }
}
