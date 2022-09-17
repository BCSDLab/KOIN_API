package koreatech.in.controller.v2.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
public class UpdateShopMenuDTO {
    @ApiModelProperty(notes = "메뉴 고유 id", example = "1", hidden = true)
    private Integer id;

    @ApiModelProperty(notes = "상점 고유 id", example = "1", hidden = true)
    private Integer shop_id;

    @NotNull(message = "메뉴 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "메뉴 이름", example = "탕수육")
    private String name;

    @NotNull(message = "단일 메뉴 여부는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "단일 메뉴 여부", example = "true")
    private Boolean is_single;

    @ApiModelProperty(notes = "단일 메뉴일때의 가격", example = "12000")
    private Integer single_price;

    @ApiModelProperty(notes = "단일 메뉴가 아닐때의 옵션에 따른 가격 리스트",
            example = "[{\"price\":5000,\"option\":\"보통\"},{\"price\":6000,\"option\":\"곱빼기\"}]")
    private List<Map<String, Object>> option_prices;

    @NotNull(message = "메뉴 당 카테고리는 최소 1개 선택되어야 합니다.")
    @ApiModelProperty(notes = "선택된 카테고리 고유 id 리스트", example = "[1, 2, 3]")
    private List<Integer> category_ids;

    @ApiModelProperty(notes = "메뉴 구성 설명")
    private String description;

    private List<MultipartFile> images;

    public UpdateShopMenuDTO init(Integer shop_id, Integer menu_id, List<MultipartFile> images) {
        this.shop_id = shop_id;
        this.id = menu_id;
        this.images = images;
        return this;
    }

    public boolean existOfOptionDuplicate() {
        if (this.option_prices == null
                || this.option_prices.size() == 0 || this.option_prices.size() == 1) {
            return false;
        }

        for (int i = 0; i < this.option_prices.size() - 1; i++) {
            String prevOption = (String) this.option_prices.get(i).get("option");
            for (int j = i + 1; j < this.option_prices.size(); j++) {
                String nextOption = (String) this.option_prices.get(j).get("option");
                if (prevOption.equals(nextOption)) {
                    return true;
                }
            }
        }

        return false;
    }
}
