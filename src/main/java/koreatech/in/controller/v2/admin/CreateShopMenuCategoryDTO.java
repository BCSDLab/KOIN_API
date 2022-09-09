package koreatech.in.controller.v2.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
public class CreateShopMenuCategoryDTO {
    // TODO: 기획 변경에 따라 최대 글자수 변경
    @Size(max = 15, message = "카테고리 이름은 최대 15자입니다.")
    @NotNull(message = "카테고리 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "카테고리 이름", example = "기본 메뉴")
    private List<String> list;
}
