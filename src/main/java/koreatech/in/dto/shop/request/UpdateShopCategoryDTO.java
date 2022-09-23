package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
public class UpdateShopCategoryDTO {
    @ApiModelProperty(notes = "상점 카테고리 고유 id", hidden = true)
    private Integer id;

    // TODO: 최대 길이 기획 나오면 적용하기
    @NotNull(message = "카테고리명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "카테고리명")
    private String name;

    @ApiModelProperty(notes = "상점 카테고리 이미지")
    private MultipartFile image;

    public UpdateShopCategoryDTO init(Integer id, MultipartFile image) {
        this.id = id;
        this.image = image;
        return this;
    }
}
