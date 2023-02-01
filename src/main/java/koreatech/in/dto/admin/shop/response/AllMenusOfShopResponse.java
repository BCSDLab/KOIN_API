package koreatech.in.dto.admin.shop.response;

import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.mapstruct.admin.shop.AdminShopMenuConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 *   특정 상점의 모든 메뉴를 응답
 */

@Getter @Builder
public class AllMenusOfShopResponse {
    private Integer count;
    private List<Menu> menus;

    @Getter @Builder
    public static class Menu {
        private Integer id;
        private String name;
        private Boolean is_hidden;
        private Boolean is_single;
        private Integer single_price;
        private List<OptionPrice> option_prices;
        private List<Integer> category_ids;

        @Getter @Builder
        public static class OptionPrice {
            private String option;
            private Integer price;
        }
    }

    public static AllMenusOfShopResponse from(List<ShopMenuProfile> shopMenuProfiles) {
        return AllMenusOfShopResponse.builder()
                .count(shopMenuProfiles.size())
                .menus(
                        shopMenuProfiles.stream()
                                .map(AdminShopMenuConverter.INSTANCE::toAllMenusOfShopResponse$Menu)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
