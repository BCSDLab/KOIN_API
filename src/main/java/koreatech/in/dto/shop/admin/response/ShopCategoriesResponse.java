package koreatech.in.dto.shop.admin.response;

import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.mapstruct.shop.admin.AdminShopCategoryMapper;
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

    @Getter @Builder
    public static final class Category {
        private Integer id;
        private String name;
        private String image_url;
    }

    public static ShopCategoriesResponse of(Integer totalCount, Integer totalPage, Integer currentPage, List<ShopCategory> shopCategories) {
        return ShopCategoriesResponse.builder()
                .total_count(totalCount)
                .current_count(shopCategories.size())
                .total_page(totalPage)
                .current_page(currentPage)
                .categories(
                        shopCategories.stream()
                                .map(AdminShopCategoryMapper.INSTANCE::toShopCategoriesResponse$Category)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
