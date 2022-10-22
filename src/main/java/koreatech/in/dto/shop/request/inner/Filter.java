package koreatech.in.dto.shop.request.inner;

import lombok.Getter;

import java.util.List;

@Getter
public class Filter {
    private List<True> trues;
    private Integer category_id;

    @Getter
    private enum True {
        IS_DELETED(1),
        DELIVERY(2),
        PAY_CARD(3),
        PAY_BANK(4);

        True(int value) {
            this.value = value;
        }

        private final int value;
    }
}
