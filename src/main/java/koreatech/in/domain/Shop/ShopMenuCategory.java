package koreatech.in.domain.Shop;

import lombok.Getter;

import java.util.Date;

@Getter
public class ShopMenuCategory {
    private Integer id;
    private String name;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuCategory() {}

    public ShopMenuCategory(String categoryName) {
        this.name = categoryName;
    }

    public void changeDeleteStatus() {
        this.is_deleted = !(this.is_deleted);
    }
}
