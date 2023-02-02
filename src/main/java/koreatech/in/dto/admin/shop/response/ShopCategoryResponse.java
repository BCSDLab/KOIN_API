package koreatech.in.dto.admin.shop.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class ShopCategoryResponse {
    @ApiModelProperty(notes = "고유 id", required = true)
    private Integer id;

    @ApiModelProperty(notes = "이름", required = true)
    private String name;

    @ApiModelProperty(notes = "이미지 URL", required = true)
    private String image_url;
}
