package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.admin.request.CreateShopRequest;
import koreatech.in.dto.shop.admin.request.UpdateShopRequest;
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

    public static Shop create(CreateShopRequest request, Integer ownerId) {
        return new Shop(request, ownerId);
    }

    private Shop(CreateShopRequest request, Integer ownerId) {
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

    public void matchOwnerId(Integer ownerId) {
        this.owner_id = ownerId;
    }
}
