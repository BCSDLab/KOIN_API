package koreatech.in.dto.shop.request.inner;

import lombok.Getter;

import java.time.DayOfWeek;

@Getter
public class Open {
    private DayOfWeek day_of_week;
    private Boolean closed;
    private String open_time;
    private String close_time;
}
