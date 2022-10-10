package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
public class CreateShopMenuCategoryDTO {
    @ApiModelProperty(notes = "상점 고유 id", hidden = true)
    private Integer shop_id;

    @Size(min = 1, max = 15, message = "카테고리명은 1자 이상 15자 이하입니다.")
    @NotNull(message = "카테고리명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "카테고리명", example = "이벤트 메뉴")
    private String name;

    public CreateShopMenuCategoryDTO init(Integer shop_id) {
        this.shop_id = shop_id;
        return this;
    }
}
