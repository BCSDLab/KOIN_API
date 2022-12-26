package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.admin.request.CreateShopMenuRequest;
import koreatech.in.dto.shop.admin.request.UpdateShopMenuRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopMenu {
    private Integer id;
    private Integer shop_id;
    private String name;
    private String description;
    private Boolean is_hidden;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public static ShopMenu create(Integer shopId, CreateShopMenuRequest request) {
        return new ShopMenu(shopId, request);
    }

    private ShopMenu(Integer shopId, CreateShopMenuRequest request) {
        this.shop_id = shopId;
        this.name = request.getName();
        this.description = request.getDescription();
        this.is_hidden = false;
    }

    public void update(UpdateShopMenuRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
    }

    public boolean hasSameShopId(Integer shopId) {
        if (this.shop_id == null || shopId == null) {
            return false;
        }

        return this.shop_id.equals(shopId);
    }

    public ShopMenu reverseIsHidden() {
        this.is_hidden = !this.is_hidden;
        return this;
    }

    public boolean needToUpdate(UpdateShopMenuRequest request) {
        return !Objects.equals(this.name, request.getName())
                || !Objects.equals(this.description, request.getDescription());
    }
}
