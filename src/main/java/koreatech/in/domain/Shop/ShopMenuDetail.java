package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopMenuDetail {
    private Integer id;
    private Integer shop_menu_id;
    private String option;
    private Integer price;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuDetail(Integer shop_menu_id, Integer price) {
        this.shop_menu_id = shop_menu_id;
        this.option = null;
        this.price = price;
    }

    public ShopMenuDetail(Integer shop_menu_id, String option, Integer price) {
        this.shop_menu_id = shop_menu_id;
        this.option = option;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ShopMenuDetail)) {
            return false;
        }

        return Objects.equals(this.shop_menu_id, ((ShopMenuDetail) obj).getShop_menu_id())
                && Objects.equals(this.option, ((ShopMenuDetail) obj).getOption())
                && Objects.equals(this.price, ((ShopMenuDetail) obj).getPrice());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.shop_menu_id)
                .append(this.option)
                .append(this.price)
                .toHashCode();
    }
}

