package koreatech.in.dto.normal.shop.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateMenuCategoryRequest {
    @ApiModelProperty(notes = "상점 카테고리 고유 id", example = "1", required = true)
    private Integer id;

    @Size(min = 1, max = 15, message = "이름의 길이는 1자 이상 15자 이하입니다.")
    @NotNull(message = "이름은 필수입니다.")
    @ApiModelProperty(notes = "카테고리명 \n" +
            "- not null \n" +
            "- 1자 이상 15자 이하", example = "사이드 메뉴", required = true)
    private String name;

}
