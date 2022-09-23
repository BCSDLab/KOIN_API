package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
public class CreateShopCategoryDTO {
    // TODO: 최대 길이 기획 나오면 적용하기
    @NotNull(message = "카테고리명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "카테고리명")
    private String name;

    @ApiModelProperty(notes = "상점 카테고리 이미지", hidden = true)
    private MultipartFile image;

    public CreateShopCategoryDTO init(MultipartFile image) {
        this.image = image;
        return this;
    }
}
