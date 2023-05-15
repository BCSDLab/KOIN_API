package koreatech.in.dto.admin.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.mapstruct.admin.shop.AdminShopMenuConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
@ApiModel("AdminAllMenusOfShopResponse")
public class AllMenusOfShopResponse {
    @ApiModelProperty(notes = "개수", example = "20", required = true)
    private Integer count;

    @ApiModelProperty(notes = "카테고리 별로 분류된 소속 메뉴 리스트", required = true)
    private List<Category> menu_categories;

    @Getter @Builder
    @ApiModel("CategoryModel_2")
    public static class Category {
        @ApiModelProperty(notes = "카테고리 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "카테고리 이름", example = "중식", required = true)
        private String name;

        @ApiModelProperty(notes = "해당 상점의 모든 메뉴 리스트", required = true)
        private List<Menu> menus;
    }

    @Getter @Builder
    @ApiModel("Menu_1")
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

        @ApiModelProperty(notes = "이미지 URL 리스트")
        private List<String> image_urls;

        @Getter @Builder
        @ApiModel("OptionPrice_2")
        public static class OptionPrice {
            @ApiModelProperty(notes = "옵션명", example = "대", required = true)
            private String option;

            @ApiModelProperty(notes = "가격", example = "26000", required = true)
            private Integer price;
        }
    }

    public static AllMenusOfShopResponse from(List<ShopMenuProfile> shopMenuProfiles, List<ShopCategory> categoryNames) {
        List<Category> category = new ArrayList<>();
        List<Integer> categoryIds = shopMenuProfiles.stream()
                .map(ShopMenuProfile::getCategory_ids)
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        for (int i = 0; i < categoryIds.size(); i++) {
            int categoryIndex = categoryIds.get(i);
            category.add(
                    AllMenusOfShopResponse.Category.builder()
                            .id(categoryIndex)
                            .name(
                                    categoryNames.stream()
                                            .filter(categoryName -> categoryName.getId().equals(categoryIndex))
                                            .collect(Collectors.toList()).get(0).getName()
                            )
                            .menus(
                                    shopMenuProfiles.stream()
                                            .filter(menuProfile -> menuProfile.getCategory_ids().contains(categoryIndex))
                                            .map(AdminShopMenuConverter.INSTANCE::toAllMenusOfShopResponse$Menu)
                                            .collect(Collectors.toList())
                            )
                            .build()
            );
        }
        return AllMenusOfShopResponse.builder()
                .count(shopMenuProfiles.size())
                .menu_categories(category)
                .build();
    }
}
