package koreatech.in.domain.Shop;

import koreatech.in.controller.v2.dto.shop.request.CreateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuDTO;
import lombok.Getter;

import java.util.Date;

@Getter
public class ShopMenu {
    private Integer id;
    private Integer shop_id;
    private String name;
    private String description;
    private Boolean is_hidden;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenu() {}

    public ShopMenu(CreateShopMenuDTO dto) {
        this.shop_id = dto.getShop_id();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.is_hidden = false;
    }

    public ShopMenu(UpdateShopMenuDTO dto) {
        this.id = dto.getId();
        this.shop_id = dto.getShop_id();
        this.name = dto.getName();
        this.description = dto.getDescription();
    }

    public ShopMenu reverseIsHidden() {
        this.is_hidden = !this.is_hidden;
        return this;
    }

    public void setIs_hidden(Boolean is_hidden) {
        this.is_hidden = is_hidden;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShopMenu)) {
            return false;
        }

        boolean descriptionEquals;
        if (this.description == null) {
            descriptionEquals = (((ShopMenu) obj).getDescription() == null);
        } else {
            descriptionEquals = this.description.equals(((ShopMenu) obj).getDescription());
        }

        return this.id.equals(((ShopMenu) obj).getId())
                && this.shop_id.equals(((ShopMenu) obj).getShop_id())
                && this.name.equals(((ShopMenu) obj).getName())
                && descriptionEquals;
    }
}
