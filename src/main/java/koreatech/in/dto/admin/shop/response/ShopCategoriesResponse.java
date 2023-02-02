package koreatech.in.dto.admin.shop.response;

import io.swagger.annotations.ApiModel;
import koreatech.in.domain.Shop.ShopCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class ShopCategoriesResponse {
    private Integer total_count;
    private Integer current_count;
    private Integer total_page;
    private Integer current_page;
    private List<Category> categories;

    @Getter
    @ApiModel("Category_1")
    private static class Category {
        private Integer id;
        private String name;
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
