package koreatech.in.dto.admin.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
public class UpdateShopCategoryRequest {
    @Size(min = 1, max = 25, message = "이름의 길이는 1자 이상 25자 이하입니다.")
    @NotNull(message = "이름은 필수입니다.")
    @ApiModelProperty(notes = "카테고리명 \n" +
                              "- not null \n" +
                              "- 1자 이상 25자 이하", example = "햄버거", required = true)
    private String name;

    @Size(max = 255, message = "이미지 URL의 길이는 최대 255자입니다.")
    @NotNull(message = "이미지는 필수입니다.")
    @ApiModelProperty(notes = "이미지 URL \n" +
                              "- not null \n" +
                              "- 최대 255자", example = "https://static.koreatech.in/test.png", required = true)
    private String image_url;
}
