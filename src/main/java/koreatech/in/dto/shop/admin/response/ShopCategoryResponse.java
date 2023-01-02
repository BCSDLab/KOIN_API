package koreatech.in.dto.shop.admin.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class ShopCategoryResponse {
    private Integer id;
    private String name;
    private String image_url;
}
