package koreatech.in.controller.v2.dto.shop.result;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter @ToString
public class ResultShopMenuDTO {
    private Integer id;
    private Integer shop_id;
    private String name;
    private List<Map<String, Object>> option_prices;
    private List<String> existent_categories;
    private List<String> selected_categories;
    private String description;
    private List<String> image_urls;
}
