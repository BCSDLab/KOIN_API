package koreatech.in.dto.admin.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class ShopCategoriesResponse {
    @ApiModelProperty(notes = "조건에 해당하는 총 상점 카테고리의 개수", example = "57", required = true)
    private Integer total_count;

    @ApiModelProperty(notes = "조건에 해당하는 상점 카테고리 중에 현재 페이지에서 조회된 수", example = "10", required = true)
    private Integer current_count;

    @ApiModelProperty(notes = "조건에 해당하는 상점 카테고리들을 조회할 수 있는 최대 페이지", example = "6", required = true)
    private Integer total_page;

    @ApiModelProperty(notes = "현재 페이지", example = "2", required = true)
    private Integer current_page;

    @ApiModelProperty(notes = "상점 카테고리 리스트", required = true)
    private List<Category> categories;

    @Getter
    @ApiModel("Category_1")
    private static class Category {
        @ApiModelProperty(notes = "고유 id", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", required = true)
        private String name;

        @ApiModelProperty(notes = "이미지 URL", required = true)
        private String image_url;

        public Category(Integer id, String name, String imageUrl) {
            this.id = id;
            this.name = name;
            this.image_url = imageUrl;
        }
    }

    public static ShopCategoriesResponse of(Integer totalCount, Integer totalPage, Integer currentPage, List<ShopCategory> shopCategories) {
        return ShopCategoriesResponse.builder()
                .total_count(totalCount)
                .current_count(shopCategories.size())
                .total_page(totalPage)
                .current_page(currentPage)
                .categories(
                        shopCategories.stream()
                                .map(shopCategory -> new Category(shopCategory.getId(), shopCategory.getName(), shopCategory.getImage_url()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
