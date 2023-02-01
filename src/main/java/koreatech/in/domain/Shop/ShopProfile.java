package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
     JOIN을 통해 상점과 관련된 정보들을 모두 담아오는 class
 */

@Getter
@NoArgsConstructor
public class ShopProfile {
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
    private Boolean is_deleted;

    @Getter
    public static final class Open {
        private DayOfWeek day_of_week;
        private Boolean closed;
        private String open_time;
        private String close_time;
    }

    @Getter
    public static final class ShopCategory {
        private Integer id;
        private String name;
    }

    @Getter
    public static final class MenuCategory {
        private Integer id;
        private String name;
    }

    public boolean isSoftDeleted() {
        if (this.is_deleted == null) {
            return false;
        }

        return this.is_deleted.equals(true);
    }

    public List<Integer> getShopCategoryIds() {
        if (this.shop_categories == null) {
            return new ArrayList<>();
        }

        return this.shop_categories.stream()
                .map(ShopCategory::getId)
                .collect(Collectors.toList());
    }

    public List<String> getShopCategoryNames() {
        if (this.shop_categories == null) {
            return new ArrayList<>();
        }

        return this.shop_categories.stream()
                .map(ShopCategory::getName)
                .collect(Collectors.toList());
    }
}
