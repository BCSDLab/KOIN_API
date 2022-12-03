package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
public class UpdateShopCategoryRequest {
    @Size(min = 1, max = 25, message = "name은 1자 이상 25자 이하입니다.")
    @NotNull(message = "name은 필수입니다.")
    @ApiModelProperty(notes = "카테고리명", example = "치킨")
    private String name;
}
