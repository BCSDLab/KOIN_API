package koreatech.in.domain.Shop;

import koreatech.in.dto.admin.shop.request.UpdateShopRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public boolean hasSameId(Integer id) {
        if (this.id == null || id == null) {
            return false;
        }

        return this.id.equals(id);
    }

    public boolean needToUpdate(UpdateShopRequest request) {
        return !Objects.equals(this.name, request.getName())
                || !Objects.equals(this.phone, request.getPhone())
                || !Objects.equals(this.address, request.getAddress())
                || !Objects.equals(this.description, request.getDescription())
                || !Objects.equals(this.delivery, request.getDelivery())
                || !Objects.equals(this.delivery_price, request.getDelivery_price())
                || !Objects.equals(this.pay_card, request.getPay_card())
                || !Objects.equals(this.pay_bank, request.getPay_bank());
    }

    public void update(UpdateShopRequest request) {
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
    }

    public boolean isDeleted() {
        if (this.is_deleted == null) {
            return false;
        }

        return this.is_deleted;
    }
}
