package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.request.CreateShopRequest;
import koreatech.in.dto.shop.request.UpdateShopRequest;
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

    public Shop(CreateShopRequest request, Integer ownerId) {
        this.owner_id = ownerId;
        this.name = request.getName();
        this.internal_name = this.name.replace(" ", "").toLowerCase();
        this.chosung = this.internal_name.substring(0, 1);
        this.phone = request.getPhone();
        this.address = request.getAddress();
        this.description = request.getDescription();
        this.delivery = request.getDelivery();
        this.delivery_price = request.getDelivery_price();
        this.pay_card = request.getPay_card();
        this.pay_bank = request.getPay_bank();
        this.is_event = false;
        this.remarks = null;
        this.hit = 0;
    }

    public Shop update(UpdateShopRequest request) {
        this.name = request.getName();
        this.phone = request.getPhone();
        this.address = request.getAddress();
        this.description = request.getDescription();
        this.delivery = request.getDelivery();
        this.delivery_price = request.getDelivery_price();
        this.pay_card = request.getPay_card();
        this.pay_bank = request.getPay_bank();
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
