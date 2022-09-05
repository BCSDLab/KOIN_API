package koreatech.in.domain.Shop;

import koreatech.in.controller.v2.dto.ShopMenuDTO;
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

    public void init(ShopMenuDTO dto) {
        this.shop_id = dto.getShop_id();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.is_hidden = false;
    }
}
