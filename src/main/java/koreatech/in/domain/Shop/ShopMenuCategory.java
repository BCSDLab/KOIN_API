package koreatech.in.domain.Shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.Date;

@Getter
public class ShopMenuCategory {
    private Integer id;
    @JsonIgnore
    private Integer shop_id;
    private String name;

    @JsonIgnore
    private Boolean is_deleted;
    @JsonIgnore
    private Date created_at;
    @JsonIgnore
    private Date updated_at;

    public ShopMenuCategory() {}

    public ShopMenuCategory(Integer shop_id, String name) {
        this.shop_id = shop_id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShopMenuCategory)) {
            return false;
        }

        return this.id.equals(((ShopMenuCategory) obj).getId());
    }
}
