package koreatech.in.controller.v2.dto.shop.result;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter @ToString
public class ShopMenuResult {
    private Integer id;
    private String name;
    private String description;
    private List<Map<String, Object>> option_prices;
    private List<String> categories;
    private List<String> image_urls;
}
