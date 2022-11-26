package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.request.CreateShopMenuDTO;
import koreatech.in.dto.shop.request.UpdateShopMenuDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopMenu {
    private Integer id;
    private Integer shop_id;
    private String name;
    private String description;
    @Setter private Boolean is_hidden;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

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

    public boolean equalsShopIdTo(Integer shopId) {
        return Objects.equals(this.shop_id, shopId);
    }

    public ShopMenu reverseIsHidden() {
        this.is_hidden = !this.is_hidden;
        return this;
    }

    public boolean needToUpdate(ShopMenu menu) {
        return !Objects.equals(this.name, menu.getName())
                || !Objects.equals(this.description, menu.getDescription());
    }
}
