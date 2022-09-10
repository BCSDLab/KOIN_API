package koreatech.in.controller.v2.dto.shop.result;

import lombok.Getter;
import lombok.ToString;

@Getter
public class ShopMenuResult {
    private Integer id;
    private String name;
    private String description;
    private Boolean is_hidden;
    private String option;
    private Integer price;
}
