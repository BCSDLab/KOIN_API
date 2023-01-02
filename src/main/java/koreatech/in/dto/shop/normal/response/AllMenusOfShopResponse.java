package koreatech.in.dto.shop.normal.response;

import koreatech.in.domain.Shop.RelatedToShopMenu;
import koreatech.in.mapstruct.shop.normal.ShopMenuMapper;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        private String description;
        private List<Integer> category_ids;
        private List<String> image_urls;

        @Getter @Builder
        public static final class OptionPrice {
            private String option;
            private Integer price;
        }
    }

    public static AllMenusOfShopResponse from(List<RelatedToShopMenu> relatedToShopMenus) {
        List<RelatedToShopMenu> noHiddenMenus = relatedToShopMenus.stream()
                .filter(menu -> !menu.isHidden())
                .collect(Collectors.toList());

        return AllMenusOfShopResponse.builder()
                .count(noHiddenMenus.size())
                .menus(
                        noHiddenMenus.stream()
                                .map(ShopMenuMapper.INSTANCE::toAllMenusOfShopResponse$Menu)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
