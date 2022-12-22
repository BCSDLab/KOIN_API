package koreatech.in.dto.shop.admin.response;

import koreatech.in.domain.Shop.ShopCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllShopCategoriesResponse {
    private Integer total_count;
    private List<Category> shop_categories;

    @Getter @Builder
    private static final class Category {
        private Integer id;
        private String name;
        private String image_url;
    }

    public static AllShopCategoriesResponse from(List<ShopCategory> shopCategories) {
        return AllShopCategoriesResponse.builder()
                .total_count(shopCategories.size())
                .shop_categories(
                        shopCategories.stream()
                               .map(shopCategory ->
                                       Category.builder()
                                               .id(shopCategory.getId())
                                               .name(shopCategory.getName())
                                               .image_url(shopCategory.getImage_url())
                                               .build()
                               )
                                .collect(Collectors.toCollection(ArrayList::new))
                )
                .build();
    }
}
