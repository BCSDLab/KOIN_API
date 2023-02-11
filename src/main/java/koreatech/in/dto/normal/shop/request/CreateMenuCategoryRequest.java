package koreatech.in.dto.normal.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
public class CreateMenuCategoryRequest {
    @Size(min = 1, max = 15, message = "이름의 길이는 1자 이상 15자 이하입니다.")
    @NotNull(message = "name은 필수입니다.")
    @ApiModelProperty(notes = "카테고리명 \n" +
                              "- not null \n" +
                              "- 1자 이상 15자 이하", example = "사이드 메뉴", required = true)
    private String name;
}
