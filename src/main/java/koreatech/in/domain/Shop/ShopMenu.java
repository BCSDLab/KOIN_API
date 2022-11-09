package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.request.CreateShopMenuDTO;
import koreatech.in.dto.shop.request.UpdateShopMenuDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
public class ShopMenu {
    private Integer id;
    private Integer shop_id;
    private String name;
    private String description;
    @Setter private Boolean is_hidden;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenu() {}

    public ShopMenu(Integer shopId, CreateShopMenuDTO dto) {
        this.shop_id = shopId;
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.is_hidden = false;
    }

    public ShopMenu(Integer shopId, Integer menuId, UpdateShopMenuDTO dto) {
        this.id = menuId;
        this.shop_id = shopId;
        this.name = dto.getName();
        this.description = dto.getDescription();
    }

    public ShopMenu reverseIsHidden() {
        this.is_hidden = !this.is_hidden;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShopMenu)) {
            return false;
        }

        // description은 nullable이므로 NullPointerException 방지
        boolean descriptionEquals = (this.description == null) ?
                (((ShopMenu) obj).getDescription() == null) : (this.description.equals(((ShopMenu) obj).getDescription()));

        return this.id.equals(((ShopMenu) obj).getId())
                && this.shop_id.equals(((ShopMenu) obj).getShop_id())
                && this.name.equals(((ShopMenu) obj).getName())
                && descriptionEquals;
    }
}
