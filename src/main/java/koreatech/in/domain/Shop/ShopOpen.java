package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.Date;

@NoArgsConstructor
@Getter
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

    public ShopOpen(Integer shop_id, DayOfWeek day_of_week) {
        this.shop_id = shop_id;
        this.day_of_week = day_of_week;
        this.closed = false;
        this.open_time = null;
        this.close_time = null;
    }

    public ShopOpen(Integer shop_id, DayOfWeek day_of_week, Boolean closed, String open_time, String close_time) {
        this.shop_id = shop_id;
        this.day_of_week = day_of_week;
        this.closed = closed;
        this.open_time = open_time;
        this.close_time = close_time;
    }
}
