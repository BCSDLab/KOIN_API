package koreatech.in.dto.shop.normal.response;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;

@Getter @Builder
public class AllShopsResponse {
    private Integer total_count;
    private List<Shop> shops;

    @Getter
    public static final class Shop {
        private Integer id;
        private String name;
        private String phone;
        private Boolean delivery;
        private Boolean pay_card;
        private Boolean pay_bank;
        private List<Open> open;
        private List<Integer> category_ids;

        @Getter
        private static final class Open {
            private DayOfWeek day_of_week;
            private Boolean closed;
            private String open_time;
            private String close_time;
        }
    }
}
