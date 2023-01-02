package koreatech.in.dto.shop.normal.response;

import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.mapstruct.shop.normal.ShopCategoryMapper;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllShopCategoriesResponse {
    private Integer total_count;
    private List<Category> shop_categories;

    @Getter @Builder
    public static final class Category {
        private Integer id;
        private String name;
        private String image_url;
    }

    public static AllShopCategoriesResponse from(List<ShopCategory> shopCategories) {
        return AllShopCategoriesResponse.builder()
                .total_count(shopCategories.size())
                .shop_categories(
                        shopCategories.stream()
                                .map(ShopCategoryMapper.INSTANCE::toAllShopCategoriesResponse$Category)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
