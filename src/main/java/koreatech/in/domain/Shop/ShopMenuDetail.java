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

        // option은 nullable이므로 NullPointerException 방지
        boolean optionEquals = (this.option == null) ?
                (((ShopMenuDetail) obj).getOption() == null) : (this.option.equals(((ShopMenuDetail) obj).getOption()));

        return this.shop_menu_id.equals(((ShopMenuDetail) obj).getShop_menu_id())
                && optionEquals
                && this.price.equals(((ShopMenuDetail) obj).getPrice());
    }
}

