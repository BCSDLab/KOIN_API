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
    private Date created_at;
    private Date updated_at;

    // single menu용 shopMenuDetail 객체 생성 메소드
    public static ShopMenuDetail singleOf(Integer shopMenuId, Integer price) {
        return new ShopMenuDetail(shopMenuId, null, price);
    }

    public static ShopMenuDetail of(Integer shopMenuId, String option, Integer price) {
        return new ShopMenuDetail(shopMenuId, option, price);
    }

    private ShopMenuDetail(Integer shopMenuId, String option, Integer price) {
        this.shop_menu_id = shopMenuId;
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

