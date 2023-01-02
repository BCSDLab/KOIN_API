package koreatech.in.dto.shop.normal.response;

import koreatech.in.domain.Shop.RelatedToShop;
import koreatech.in.mapstruct.shop.admin.AdminShopMapper;
import koreatech.in.mapstruct.shop.normal.ShopMapper;
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
    public static final class Shop {
        private Integer id;
        private String name;
        private String phone;
        private Boolean delivery;
        private Boolean pay_card;
        private Boolean pay_bank;
        private List<Open> open;
        private List<Integer> category_ids;

        @Getter @Builder
        public static final class Open {
            private DayOfWeek day_of_week;
            private Boolean closed;
            private String open_time;
            private String close_time;
        }
    }

    public static AllShopsResponse from(List<RelatedToShop> shops) {
        return AllShopsResponse.builder()
                .count(shops.size())
                .shops(
                        shops.stream()
                                .map(ShopMapper.INSTANCE::toAllShopsResponse$Shop)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
