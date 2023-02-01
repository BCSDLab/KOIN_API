package koreatech.in.dto.shop.admin.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class MenuResponse {
    private Integer id;
    private Integer shop_id;
    private String name;
    private Boolean is_hidden;
    private Boolean is_single;
    private Integer single_price;
    private List<OptionPrice> option_prices;
    private String description;
    private List<Integer> category_ids;
    private List<String> image_urls;

    @Getter @Builder
    public static class OptionPrice {
        private String option;
        private Integer price;
    }
}
