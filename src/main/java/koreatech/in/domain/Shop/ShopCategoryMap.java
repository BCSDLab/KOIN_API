package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopCategoryMap {
    private Integer id;
    private Integer shop_id;
    private Integer shop_category_id;
    private Date created_at;
    private Date updated_at;

    public static ShopCategoryMap of(Integer shopId, Integer shopCategoryId) {
        return new ShopCategoryMap(shopId, shopCategoryId);
    }

    private ShopCategoryMap(Integer shopId, Integer shopCategoryId) {
        this.shop_id = shopId;
        this.shop_category_id = shopCategoryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ShopCategoryMap)) {
            return false;
        }

        return Objects.equals(this.shop_id, ((ShopCategoryMap) obj).getShop_id())
                && Objects.equals(this.shop_category_id, ((ShopCategoryMap) obj).getShop_category_id());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.shop_id)
                .append(this.shop_category_id)
                .toHashCode();
    }
}
