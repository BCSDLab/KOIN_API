package koreatech.in.controller.v2.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter @Setter
@ToString
public class ShopMenuDTO {
    private Integer id;
    private Integer shop_id;
    private String name;
    private Boolean is_single;
    private Integer single_price;
    private List<Map<String, Integer>> option_prices;
    private List<Integer> category_ids;
    private String description;
    private List<String> image_urls;
}
