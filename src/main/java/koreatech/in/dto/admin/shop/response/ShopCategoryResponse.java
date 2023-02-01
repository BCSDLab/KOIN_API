package koreatech.in.dto.admin.shop.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class ShopCategoryResponse {
    private Integer id;
    private String name;
    private String image_url;
}
