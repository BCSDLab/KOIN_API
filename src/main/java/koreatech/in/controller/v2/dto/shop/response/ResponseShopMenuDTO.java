package koreatech.in.controller.v2.dto.shop.response;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ResponseShopMenuDTO {
    private String name;
    private Boolean is_single;
    private Integer single_price;
    private List<Map<String, Integer>> option_prices;
    private List<String> existent_categories;
    private List<String> selected_categories;
    private String description;
    private List<String> image_urls;
}
