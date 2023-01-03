package koreatech.in.dto.shop.admin.response;

import koreatech.in.domain.Shop.ShopMenuCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 *   특정 상점의 모든 메뉴 카테고리를 응답
 */

@Getter @Builder
public class AllMenuCategoriesOfShopResponse {
    private Integer count;
    private List<Category> menu_categories;

    @Getter @Builder
    private static class Category {
        private Integer id;
        private String name;

        public static Category of(Integer id, String name) {
            return Category.builder()
                    .id(id)
                    .name(name)
                    .build();
        }
    }

    public static AllMenuCategoriesOfShopResponse from(List<ShopMenuCategory> shopMenuCategories) {
        return AllMenuCategoriesOfShopResponse.builder()
                .count(shopMenuCategories.size())
                .menu_categories(
                        shopMenuCategories.stream()
                                .map(shopMenuCategory -> Category.of(shopMenuCategory.getId(), shopMenuCategory.getName()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}