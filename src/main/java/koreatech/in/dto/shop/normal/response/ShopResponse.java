package koreatech.in.dto.shop.normal.response;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;

@Getter @Builder
public class ShopResponse {
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private String description;
    private Boolean delivery;
    private Integer delivery_price;
    private Boolean pay_card;
    private Boolean pay_bank;
    private List<Open> open;
    private List<String> image_urls;
    private List<ShopCategory> shop_categories;
    private List<MenuCategory> menu_categories;

    @Getter @Builder
    public static final class Open {
        private DayOfWeek day_of_week;
        private Boolean closed;
        private String open_time;
        private String close_time;
    }

    @Getter @Builder
    public static final class ShopCategory {
        private Integer id;
        private String name;
    }

    @Getter @Builder
    public static final class MenuCategory {
        private Integer id;
        private String name;
    }
}
