package koreatech.in.controller.v2.admin;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateShopMenuCategoryDTO {
    @Size(max = 10, message = "카테고리 이름은 최대 10자입니다.")
    @NotBlank(message = "카테고리 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "카테고리 이름", example = "사이드 메뉴")
    private String name;

    public String getName() {
        return name;
    }
}
