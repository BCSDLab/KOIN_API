package koreatech.in.controller.v2.dto.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ShopMenuResultDTO {
    private Integer id;
    private Integer shop_id;
    private String name;
    private String option;
    private Integer price;
    private List<String> categories;
    private String description;
    private List<String> image_urls;

    public ShopMenuResultDTO() {
    }
}
