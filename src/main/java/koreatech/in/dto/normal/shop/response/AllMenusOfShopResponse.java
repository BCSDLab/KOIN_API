package koreatech.in.dto.normal.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.mapstruct.normal.shop.ShopMenuConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllMenusOfShopResponse {
    @ApiModelProperty(notes = "개수", example = "20", required = true)
    private Integer count;

    @ApiModelProperty(notes = "해당 상점의 모든 메뉴 리스트", required = true)
    private List<Menu> menus;

    @Getter @Builder
    @ApiModel("Menu_2")
    public static class Menu {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "탕수육", required = true)
        private String name;

        @ApiModelProperty(notes = "숨김 여부", example = "false", required = true)
        private Boolean is_hidden;

        @ApiModelProperty(notes = "단일 메뉴 여부 \n" +
                                  "- true: 단일 메뉴 \n" +
                                  "- false: 옵션이 있는 메뉴", example = "false", required = true)
        private Boolean is_single;

        @ApiModelProperty(notes = "단일 메뉴일때(is_single이 true일때)의 가격", example = "10000")
        private Integer single_price;

        @ApiModelProperty(notes = "옵션이 있는 메뉴일때(is_single이 false일때)의 옵션에 따른 가격 리스트")
        private List<OptionPrice> option_prices;

        @ApiModelProperty(notes = "설명", example = "저희 식당의 대표 메뉴 탕수육입니다.")
        private String description;

        @ApiModelProperty(notes = "소속되어 있는 메뉴 카테고리들의 고유 id 리스트", required = true)
        private List<Integer> category_ids;

        @ApiModelProperty(notes = "이미지 URL 리스트")
        private List<String> image_urls;

        @Getter @Builder
        @ApiModel("OptionPrice_5")
        public static class OptionPrice {
            @ApiModelProperty(notes = "옵션명", example = "대", required = true)
            private String option;

            @ApiModelProperty(notes = "가격", example = "26000", required = true)
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
