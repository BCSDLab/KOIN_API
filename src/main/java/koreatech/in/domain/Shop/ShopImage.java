package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopImage {
    private Integer id;
    private Integer shop_id;
    private String image_url;
    private Date created_at;
    private Date updated_at;

    public static ShopImage of(Integer shopId, String imageUrl) {
        return new ShopImage(shopId, imageUrl);
    }

    private ShopImage(Integer shopId, String imageUrl) {
        this.shop_id = shopId;
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
        if (!(obj instanceof ShopImage)) {
            return false;
        }

        return Objects.equals(this.shop_id, ((ShopImage) obj).getShop_id())
                && Objects.equals(this.image_url, ((ShopImage) obj).getImage_url());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.shop_id)
                .append(this.image_url)
                .toHashCode();
    }
}
