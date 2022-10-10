package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Getter
public class ShopCategoryMap {
    private Integer id;
    private Integer shop_id;
    private Integer shop_category_id;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopCategoryMap(Integer shop_id, Integer shop_category_id) {
        this.shop_id = shop_id;
        this.shop_category_id = shop_category_id;
    }
}
