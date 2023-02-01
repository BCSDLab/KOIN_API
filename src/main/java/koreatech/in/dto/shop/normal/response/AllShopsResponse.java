package koreatech.in.dto.shop.normal.response;

import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.mapstruct.shop.normal.ShopConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllShopsResponse {
    private Integer count;
    private List<Shop> shops;

    @Getter @Builder
    public static class Shop {
        private Integer id;
        private String name;
        private String phone;
        private Boolean delivery;
        private Boolean pay_card;
        private Boolean pay_bank;
        private List<Open> open;
        private List<Integer> category_ids;

        @Getter @Builder
        public static class Open {
            private DayOfWeek day_of_week;
            private Boolean closed;
            private String open_time;
            private String close_time;
        }
    }

    public static AllShopsResponse from(List<ShopProfile> shopProfiles) {
        return AllShopsResponse.builder()
                .count(shopProfiles.size())
                .shops(
                        shopProfiles.stream()
                                .map(ShopConverter.INSTANCE::toAllShopsResponse$Shop)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
