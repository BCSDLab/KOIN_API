package koreatech.in.dto.normal.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopMenuCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllMenuCategoriesOfShopResponse {
    @ApiModelProperty(notes = "개수", example = "4", required = true)
    private Integer count;

    @ApiModelProperty(notes = "메뉴 카테고리 리스트", required = true)
    private List<Category> menu_categories;

    @Getter
    @ApiModel("Category_3")
    private static class Category {
        @ApiModelProperty(notes = "카테고리 고유 id", required = true)
        private Integer id;

        @ApiModelProperty(notes = "카테고리 이름", required = true)
        private String name;

        public Category(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static AllMenuCategoriesOfShopResponse from(List<ShopMenuCategory> shopMenuCategories) {
        return AllMenuCategoriesOfShopResponse.builder()
                .count(shopMenuCategories.size())
                .menu_categories(
                        shopMenuCategories.stream()
                                .map(shopMenuCategory -> new Category(shopMenuCategory.getId(), shopMenuCategory.getName()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
