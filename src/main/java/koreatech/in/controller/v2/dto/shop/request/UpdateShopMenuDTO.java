package koreatech.in.controller.v2.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
public class UpdateShopMenuDTO {
    @NotNull(message = "메뉴 고유 id는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "메뉴 고유 id", example = "1")
    private Integer id;

    @NotNull(message = "메뉴 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "메뉴 이름", example = "탕수육")
    private String name;

    @NotNull(message = "단일 메뉴 여부는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "단일 메뉴 여부", example = "true")
    private Boolean is_single;

    @ApiModelProperty(notes = "단일 메뉴일때의 가격", example = "12000")
    private Integer single_price;

    @ApiModelProperty(notes = "단일 메뉴가 아닐때의 옵션에 따른 가격", example = "[{\"소\": 10000}, {\"중\": 12000}, {\"대\": 14000}]")
    private List<Map<String, Integer>> option_prices;

    @NotNull(message = "메뉴 당 카테고리는 최소 1개 선택되어야 합니다.")
    @ApiModelProperty(notes = "선택된 카테고리명 리스트")
    private List<String> categories;

    @ApiModelProperty(notes = "메뉴 구성 설명")
    private String description;
    private List<MultipartFile> images;

    public UpdateShopMenuDTO init(Integer id, List<MultipartFile> images) {
        this.id = id;
        this.images = images;
        return this;
    }
}
