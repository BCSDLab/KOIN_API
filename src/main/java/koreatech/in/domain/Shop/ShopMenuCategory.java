package koreatech.in.domain.Shop;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class ShopMenuCategory {
    private Integer id;
    private String name;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;
}
