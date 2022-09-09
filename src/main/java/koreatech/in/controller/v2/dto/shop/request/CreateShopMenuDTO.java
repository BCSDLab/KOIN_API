package koreatech.in.controller.v2.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
public class CreateShopMenuDTO {
    @NotNull(message = "상점 고유 id는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "상점 고유 id", example = "1")
    private Integer shop_id;

    @NotNull(message = "메뉴 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "메뉴 이름", example = "탕수육")
    private String name;

    @NotNull(message = "단일 메뉴 여부는 비워둘 수 없습니다")
    @ApiModelProperty(notes = "단일 메뉴 여부", example = "true")
    private Boolean is_single;

    @ApiModelProperty(notes = "단일 메뉴일때의 가격", example = "12000")
    private Integer single_price;

    @ApiModelProperty(notes = "단일 메뉴가 아닐때의 옵션에 따른 가격")
    private List<Map<String, Integer>> option_prices;

    @NotNull(message = "메뉴 당 카테고리는 최소 1개 선택되어야 합니다.")
    @ApiModelProperty(notes = "선택된 카테고리명 리스트")
    private List<String> categories;

    @ApiModelProperty(notes = "메뉴 구성 설명")
    private String description;

    @ApiModelProperty(notes = "메뉴 이미지 리스트", hidden = true)
    private List<MultipartFile> images;

    public CreateShopMenuDTO init(List<MultipartFile> images) {
        this.images = images;
        return this;
    }
}
