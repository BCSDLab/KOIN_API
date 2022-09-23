package koreatech.in.domain.Shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Getter
public class ShopCategory {
    private Integer id;
    private String name;
    private String image_url;

    @JsonIgnore
    private Boolean is_deleted;
    @JsonIgnore
    private Date created_at;
    @JsonIgnore
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShopCategory)) {
            return false;
        }

        return this.id.equals(((ShopCategory) obj).getId());
    }
}
