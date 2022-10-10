package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.ValidationException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class CreateShopMenuDTO {
    @ApiModelProperty(notes = "상점 고유 id", hidden = true)
    private Integer shop_id;

    @Size(min = 1, max = 25, message = "메뉴 이름은 1자 이상 25자 이하입니다.")
    @NotNull(message = "메뉴 이름은 필수입니다.")
    @ApiModelProperty(notes = "메뉴 이름", example = "탕수육")
    private String name;

    @NotNull(message = "단일 메뉴 여부는 필수입니다.")
    @ApiModelProperty(notes = "단일 메뉴 여부", example = "true")
    private Boolean is_single;

    @Min(value = 0, message = "단일 메뉴일때의 가격은 0원 이상입니다.")
    @Max(value = Integer.MAX_VALUE, message = "단일 메뉴일때의 가격은 2147483647원 이하입니다.")
    @ApiModelProperty(notes = "단일 메뉴일때의 가격", example = "12000")
    private Integer single_price;

    @ApiModelProperty(notes = "단일 메뉴가 아닐때의 옵션에 따른 가격 리스트",
            example = "[{\"price\":5000,\"option\":\"보통\"},{\"price\":6000,\"option\":\"곱빼기\"}]")
    private List<Map<String, Object>> option_prices;

    @Size(min = 1, message = "메뉴 카테고리는 최소 1개 선택되어야 합니다.")
    @NotNull(message = "메뉴 카테고리 id 리스트는 필수입니다.")
    @ApiModelProperty(notes = "선택된 카테고리 고유 id 리스트", example = "[1, 2, 3]")
    private List<Integer> category_ids;

    @Size(max = 80, message = "메뉴 구성 설명은 최대 80자입니다.")
    @ApiModelProperty(notes = "메뉴 구성 설명")
    private String description;

    @ApiModelProperty(notes = "메뉴 이미지 리스트", hidden = true)
    private List<MultipartFile> images;

    public CreateShopMenuDTO init(Integer shop_id, List<MultipartFile> images) {
        this.shop_id = shop_id;
        this.images = images;
        return this;
    }

    public boolean existOfOptionDuplicate() throws Exception {
        if (this.option_prices == null || this.option_prices.size() == 0 || this.option_prices.size() == 1) {
            return false;
        }

        for (int i = 0; i < this.option_prices.size() - 1; i++) {
            String prevOption = (String) this.option_prices.get(i).get("option");

            if (prevOption == null || prevOption.isEmpty()) {
                throw new ValidationException(new ErrorMessage("단일 메뉴가 아니면 옵션을 비워둘 수 없습니다.", 0));
            }

            for (int j = i + 1; j < this.option_prices.size(); j++) {
                String nextOption = (String) this.option_prices.get(j).get("option");

                if (nextOption == null || nextOption.isEmpty()) {
                    throw new ValidationException(new ErrorMessage("단일 메뉴가 아니면 옵션을 비워둘 수 없습니다.", 0));
                }

                if (prevOption.equals(nextOption)) {
                    return true;
                }
            }
        }

        return false;
    }
}
