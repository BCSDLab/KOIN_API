package koreatech.in.domain.Shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopOpen {
    private Integer id;
    private Integer shop_id;
    private DayOfWeek day_of_week;
    private Boolean closed;
    private String open_time;
    private String close_time;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public void matchShopId(Integer shopId) {
        this.shop_id = shopId;
    }

    public static ShopOpen of(Integer shopId, DayOfWeek dayOfWeek, Boolean closed, String openTime, String closeTime) {
        return new ShopOpen(shopId, dayOfWeek, closed, openTime, closeTime);
    }

    private ShopOpen(Integer shopId, DayOfWeek dayOfWeek, Boolean closed, String openTime, String closeTime) {
        this.shop_id = shopId;
        this.day_of_week = dayOfWeek;
        this.closed = closed;
        this.open_time = openTime;
        this.close_time = closeTime;
    }
}
