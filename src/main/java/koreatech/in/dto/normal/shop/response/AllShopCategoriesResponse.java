package koreatech.in.dto.normal.shop.response;

import koreatech.in.domain.Shop.ShopCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllShopCategoriesResponse {
    private Integer total_count;
    private List<Category> shop_categories;

    @Getter @Builder
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
