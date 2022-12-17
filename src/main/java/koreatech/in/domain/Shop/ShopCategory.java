package koreatech.in.domain.Shop;

import koreatech.in.dto.shop.admin.request.CreateShopCategoryRequest;
import koreatech.in.dto.shop.admin.request.UpdateShopCategoryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ShopCategory {
    private Integer id;
    private String name;
    private String image_url;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    public static ShopCategory create(CreateShopCategoryRequest request) {
        return new ShopCategory(request);
    }

    private ShopCategory(CreateShopCategoryRequest request) {
        this.name = request.getName();
        this.image_url = request.getImage_url();
    }

    public boolean needToUpdate(UpdateShopCategoryRequest request) {
        // name과 image_url중 하나라도 다를 경우 update 필요
        return !Objects.equals(this.name, request.getName())
                || !Objects.equals(this.image_url, request.getImage_url());
    }

    public void update(UpdateShopCategoryRequest request) {
        this.name = request.getName();
        this.image_url = request.getImage_url();
    }

    public boolean hasSameId(Integer id) {
        if (this.id == null || id == null) {
            return false;
        }

        return this.id.equals(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ShopCategory)) {
            return false;
        }

        return Objects.equals(this.id, ((ShopCategory) obj).getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.id)
                .toHashCode();
    }
}
