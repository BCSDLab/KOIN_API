package koreatech.in.dto.normal.shop.response;

import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.mapstruct.normal.shop.ShopMenuConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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
        private String description;
        private List<Integer> category_ids;
        private List<String> image_urls;

        @Getter @Builder
        public static class OptionPrice {
            private String option;
            private Integer price;
        }
    }

    public static AllMenusOfShopResponse from(List<ShopMenuProfile> shopMenuProfiles) {
        List<ShopMenuProfile> unhiddenMenus = shopMenuProfiles.stream()
                .filter(menu -> !menu.isHidden())
                .collect(Collectors.toList());

        return AllMenusOfShopResponse.builder()
                .count(unhiddenMenus.size())
                .menus(
                        unhiddenMenus.stream()
                                .map(ShopMenuConverter.INSTANCE::toAllMenusOfShopResponse$Menu)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
