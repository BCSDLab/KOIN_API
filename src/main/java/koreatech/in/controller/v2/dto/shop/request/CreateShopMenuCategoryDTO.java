package koreatech.in.controller.v2.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class CreateShopMenuCategoryDTO {
    @ApiModelProperty(notes = "상점 고유 id", hidden = true)
    private Integer shop_id;

    // TODO: 최대 길이 기획 나오면 적용하기
    @NotNull(message = "카테고리명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "카테고리명", example = "이벤트 메뉴")
    private String name;

    public CreateShopMenuCategoryDTO init(Integer shop_id) {
        this.shop_id = shop_id;
        return this;
    }
}
