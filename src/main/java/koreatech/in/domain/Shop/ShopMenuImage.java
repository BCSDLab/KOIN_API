package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopMenuImage {
    private Integer id;
    private Integer shop_menu_id;
    private String image_url;
    private Date created_at;
    private Date updated_at;

    public static ShopMenuImage of(Integer shopMenuId, String imageUrl) {
        return new ShopMenuImage(shopMenuId, imageUrl);
    }

    private ShopMenuImage(Integer shopMenuId, String imageUrl) {
        this.shop_menu_id = shopMenuId;
        this.image_url = imageUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ShopMenuImage)) {
            return false;
        }

        return Objects.equals(this.shop_menu_id, ((ShopMenuImage) obj).getShop_menu_id())
                && Objects.equals(this.image_url, ((ShopMenuImage) obj).getImage_url());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.shop_menu_id)
                .append(this.image_url)
                .toHashCode();
    }
}
