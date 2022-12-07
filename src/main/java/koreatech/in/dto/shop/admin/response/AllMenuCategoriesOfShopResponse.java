package koreatech.in.dto.shop.admin.response;

import koreatech.in.dto.shop.admin.response.inner.ShopMenuCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 *   특정 상점의 모든 메뉴 카테고리를 응답
 */

@Getter @Builder
public class AllMenuCategoriesOfShopResponse {
    private Integer shop_id;
    private Integer count;
    private List<ShopMenuCategory> menu_categories;
}
