package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.ShopCategory;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponseShopCategoriesDTO {
    Integer total_page;
    Integer current_page;
    List<ShopCategory> shop_categories;

    public ResponseShopCategoriesDTO(Integer total_page, Integer current_page, List<ShopCategory> shop_categories) {
        this.total_page = total_page;
        this.current_page = current_page;
        this.shop_categories = shop_categories;
    }
}
