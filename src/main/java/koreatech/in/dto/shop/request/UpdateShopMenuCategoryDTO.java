package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateShopMenuCategoryDTO {
    @ApiModelProperty(notes = "상점 고유 id", hidden = true)
    private Integer shop_id;

    @ApiModelProperty(notes = "수정 적용할 카테고리 리스트", example = "[\"대표 메뉴\", \"전체 메뉴\"]")
    private List<String> categories;

    public UpdateShopMenuCategoryDTO init(Integer shop_id) {
        this.shop_id = shop_id;
        return this;
    }
}
