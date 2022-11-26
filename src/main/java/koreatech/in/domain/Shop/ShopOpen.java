package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.Date;

@Getter
@NoArgsConstructor
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

    public ShopOpen(Integer shopId, DayOfWeek dayOfWeek) {
        this.shop_id = shopId;
        this.day_of_week = dayOfWeek;
        this.closed = false;
        this.open_time = null;
        this.close_time = null;
    }

    public ShopOpen(Integer shopId, DayOfWeek dayOfWeek, Boolean closed, String openTime, String closeTime) {
        this.shop_id = shopId;
        this.day_of_week = dayOfWeek;
        this.closed = closed;
        this.open_time = openTime;
        this.close_time = closeTime;
    }
}
