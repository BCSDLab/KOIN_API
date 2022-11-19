package koreatech.in.domain.Shop;

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

    public ShopCategory(String name, String image_url) {
        this.name = name;
        this.image_url = image_url;
    }

    public ShopCategory update(String name, String image_url) {
        this.name = name;
        this.image_url = image_url;
        return this;
    }

    public boolean equalsIdTo(Integer id) {
        return Objects.equals(this.id, id);
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
