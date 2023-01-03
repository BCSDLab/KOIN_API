package koreatech.in.dto.shop.admin.response;

import koreatech.in.domain.Shop.RelatedToShopMenu;
import koreatech.in.mapstruct.shop.admin.AdminShopMenuMapper;
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
    public static final class Menu {
        private Integer id;
        private String name;
        private Boolean is_hidden;
        private Boolean is_single;
        private Integer single_price;
        private List<OptionPrice> option_prices;
        private List<Integer> category_ids;

        @Getter @Builder
        public static final class OptionPrice {
            private String option;
            private Integer price;
        }
    }

    public static AllMenusOfShopResponse from(List<RelatedToShopMenu> relatedToShopMenus) {
        return AllMenusOfShopResponse.builder()
                .count(relatedToShopMenus.size())
                .menus(
                        relatedToShopMenus.stream()
                                .map(AdminShopMenuMapper.INSTANCE::toAllMenusOfShopResponse$Menu)
                                .collect(Collectors.toList())
                )
                .build();
    }
}