package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.request.CreateShopDTO;
import koreatech.in.dto.shop.request.UpdateShopDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
@Getter
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
    private Integer delivery_price = 0;
    private Boolean pay_card;
    private Boolean pay_bank;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;
    private Boolean is_event;
    private String remarks;
    private Integer hit;

    public Shop(CreateShopDTO dto) {
        this.owner_id = 0; // TODO: 수정 필요
        this.name = dto.getName();
        this.internal_name = this.name.replace(" ", "").toLowerCase();
        this.chosung = this.internal_name.substring(0, 1);
        this.phone = dto.getPhone();
        this.address = dto.getAddress();
        this.description = dto.getDescription();
        this.delivery = dto.getDelivery();
        this.pay_card = dto.getPay_card();
        this.pay_bank = dto.getPay_bank();
        this.is_event = null;
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
}
