package koreatech.in.domain.Shop;

import lombok.Getter;

import java.util.Date;

@Getter
public class ShopMenuCategory {
    private Integer id;
    private Integer shop_id;
    private String name;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuCategory() {}

    public ShopMenuCategory(Integer shop_id, String name) {
        this.shop_id = shop_id;
        this.name = name;
    }

    public void changeDeleteStatus() {
        this.is_deleted = !(this.is_deleted);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShopMenuCategory)) {
            return false;
        }

        return this.shop_id.equals(((ShopMenuCategory) obj).getShop_id())
                && this.name.equals(((ShopMenuCategory) obj).getName());
    }
}
