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
    private List<String> categories;
    private String description;
    private List<String> image_urls;

    public void setForSingleMenu(Integer single_price) {
        this.is_single = true;
        this.single_price = single_price;
    }

    public void setForOptionMenu(List<Map<String, Integer>> option_prices) {
        this.is_single = false;
        this.option_prices = option_prices;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setImageUrls(List<String> image_urls) {
        this.image_urls = image_urls;
    }
}
