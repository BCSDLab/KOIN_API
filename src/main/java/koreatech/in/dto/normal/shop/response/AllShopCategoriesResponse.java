package koreatech.in.dto.normal.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllShopCategoriesResponse {
    @ApiModelProperty(notes = "개수", example = "10", required = true)
    private Integer total_count;

    @ApiModelProperty(notes = "모든 상점 카테고리 리스트", required = true)
    private List<Category> shop_categories;

    @Getter
    @AllArgsConstructor
    @ApiModel("Category_4")
    private static class Category {
        @ApiModelProperty(notes = "고유 id", example = "2", required = true)
        private final Integer id;

        @ApiModelProperty(notes = "이름", example = "치킨", required = true)
        private final String name;

        @ApiModelProperty(notes = "이미지 URL", example = "https://static.koreatech.in/test.png", required = true)
        private final String image_url;
    }

    public static AllShopCategoriesResponse from(List<ShopCategory> shopCategories) {
        return AllShopCategoriesResponse.builder()
                .total_count(shopCategories.size())
                .shop_categories(
                        shopCategories.stream()
                                .map(shopCategory -> new Category(shopCategory.getId(), shopCategory.getName(), shopCategory.getImage_url()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
