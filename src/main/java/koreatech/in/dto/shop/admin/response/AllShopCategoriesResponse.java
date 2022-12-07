package koreatech.in.dto.shop.admin.response;

import koreatech.in.dto.shop.admin.response.inner.ShopCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class AllShopCategoriesResponse {
    private Integer total_count;
    private List<ShopCategory> shop_categories;
}
