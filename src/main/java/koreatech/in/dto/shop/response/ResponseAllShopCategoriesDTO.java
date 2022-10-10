package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.ShopCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class ResponseAllShopCategoriesDTO {
    private Integer total_count;
    private List<ShopCategory> shop_categories;
}
