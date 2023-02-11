package koreatech.in.dto.admin.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.mapstruct.admin.shop.AdminShopMenuConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
@ApiModel("AdminAllMenusOfShopResponse")
public class AllMenusOfShopResponse {
    @ApiModelProperty(notes = "개수", example = "20", required = true)
    private Integer count;

    @ApiModelProperty(notes = "전체 메뉴 리스트", required = true)
    private List<Menu> menus;

    @Getter @Builder
    @ApiModel("Menu_1")
    public static class Menu {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "탕수육", required = true)
        private String name;

        @ApiModelProperty(notes = "숨김 여부", example = "false", required = true)
        private Boolean is_hidden;

        @ApiModelProperty(notes = "단일 메뉴 여부", example = "true", required = true)
        private Boolean is_single;

        @ApiModelProperty(notes = "단일 메뉴일때(is_single이 true일때)의 가격", example = "10000")
        private Integer single_price;

        @ApiModelProperty(notes = "옵션이 있는 메뉴일때(is_single이 false일때)의 가격")
        private List<OptionPrice> option_prices;

        @ApiModelProperty(notes = "소속되어 있는 메뉴 카테고리 고유 id 리스트", required = true)
        private List<Integer> category_ids;

        @Getter @Builder
        @ApiModel("OptionPrice_2")
        public static class OptionPrice {
            @ApiModelProperty(notes = "옵션명", example = "소", required = true)
            private String option;

            @ApiModelProperty(notes = "옵션에 대한 가격", example = "10000", required = true)
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
