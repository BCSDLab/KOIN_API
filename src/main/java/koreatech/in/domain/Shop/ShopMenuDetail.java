package koreatech.in.domain.Shop;

import lombok.Getter;

import java.util.Date;

@Getter
public class ShopMenuDetail {
    private Integer id;
    private Integer shop_menu_id;
    private String option;
    private Integer price;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public ShopMenuDetail() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShop_menu_id() {
        return shop_menu_id;
    }

    public void setShop_menu_id(Integer shop_menu_id) {
        this.shop_menu_id = shop_menu_id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public ShopMenuDetail(Integer shop_menu_id, Integer price) {
        this.shop_menu_id = shop_menu_id;
        this.option = null;
        this.price = price;
    }

    public ShopMenuDetail(Integer shop_menu_id, String option, Integer price) {
        this.shop_menu_id = shop_menu_id;
        this.option = option;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShopMenuDetail)) {
            return false;
        }

        return this.shop_menu_id.equals(((ShopMenuDetail) obj).getShop_menu_id())
                && this.option.equals(((ShopMenuDetail) obj).getOption())
                && this.price.equals(((ShopMenuDetail) obj).getPrice());
    }
}

