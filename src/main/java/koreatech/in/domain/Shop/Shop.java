package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.request.CreateShopDTO;
import koreatech.in.dto.shop.request.UpdateShopDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@NoArgsConstructor
public class Shop {
    private Integer id;
    private Integer owner_id;
    private String name;
    private String internal_name;
    private String chosung;
    private String phone;
    private String address;
    private String description;
    private Boolean delivery;
    private Integer delivery_price;
    private Boolean pay_card;
    private Boolean pay_bank;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;
    private Boolean is_event;
    private String remarks;
    private Integer hit;

    public Shop(CreateShopDTO dto, Integer ownerId) {
        this.owner_id = ownerId;
        this.name = dto.getName();
        this.internal_name = this.name.replace(" ", "").toLowerCase();
        this.chosung = this.internal_name.substring(0, 1);
        this.phone = dto.getPhone();
        this.address = dto.getAddress();
        this.description = dto.getDescription();
        this.delivery = dto.getDelivery();
        this.delivery_price = dto.getDelivery_price();
        this.pay_card = dto.getPay_card();
        this.pay_bank = dto.getPay_bank();
        this.is_event = false;
        this.remarks = null;
        this.hit = 0;
    }

    public Shop update(UpdateShopDTO dto) {
        this.name = dto.getName();
        this.phone = dto.getPhone();
        this.address = dto.getAddress();
        this.description = dto.getDescription();
        this.delivery = dto.getDelivery();
        this.delivery_price = dto.getDelivery_price();
        this.pay_card = dto.getPay_card();
        this.pay_bank = dto.getPay_bank();
        this.internal_name = this.name.replace(" ", "").toLowerCase();
        this.chosung = this.internal_name.substring(0, 1);

        return this;
    }

    public boolean equalsIdTo(Integer id) {
        return Objects.equals(this.id, id);
    }

    public Shop matchOwnerId(Integer ownerId) {
        this.owner_id = ownerId;
        return this;
    }
}
