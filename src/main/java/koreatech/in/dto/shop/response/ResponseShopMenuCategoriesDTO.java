package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.ShopMenuCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class ResponseShopMenuCategoriesDTO {
    private Integer shop_id;
    private List<ShopMenuCategory> menu_categories;
}
