package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.ShopMenuCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class AllMenuCategoriesResponse {
    private Integer shop_id;
    private Integer count;
    private List<ShopMenuCategory> menu_categories;
}
