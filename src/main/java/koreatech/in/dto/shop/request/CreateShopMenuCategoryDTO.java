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

    @Size(min = 1, max = 15, message = "name은 1자 이상 15자 이하입니다.")
    @NotNull(message = "name은 필수입니다.")
    @ApiModelProperty(notes = "카테고리명", example = "사이드 메뉴")
    private String name;

    public CreateShopMenuCategoryDTO init(Integer shop_id) {
        this.shop_id = shop_id;
        return this;
    }
}
