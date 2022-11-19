package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopMenuCategory {
    private Integer id;
    private Integer shop_id;
    private String name;

    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuCategory(Integer shop_id, String name) {
        this.shop_id = shop_id;
        this.name = name;
    }

    public boolean equalsShopIdTo(Integer shopId) {
        return Objects.equals(this.shop_id, shopId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ShopMenuCategory)) {
            return false;
        }

        return Objects.equals(this.id, ((ShopMenuCategory) obj).getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.id)
                .toHashCode();
    }
}
