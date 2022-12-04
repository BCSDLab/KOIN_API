package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.shop.request.inner.OptionPrice;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class CreateShopMenuRequest {
    @Size(min = 1, max = 25, message = "name은 1자 이상 25자 이하입니다.")
    @NotNull(message = "name은 필수입니다.")
    @ApiModelProperty(notes = "메뉴명", example = "짜장면")
    private String name;

    @NotNull(message = "is_single은 필수입니다.")
    @ApiModelProperty(notes = "단일 메뉴 여부", example = "true")
    private Boolean is_single;

    @PositiveOrZero(message = "single_price는 0 이상 2147483647 이하입니다.")
    @Max(value = Integer.MAX_VALUE, message = "single_price는 0 이상 2147483647 이하입니다.")
    @ApiModelProperty(notes = "단일 메뉴일때의 가격", example = "12000")
    private Integer single_price;

    @Valid
    @Size(min = 1, message = "is_single이 false이면 option_prices의 길이는 1 이상입니다.")
    @ApiModelProperty(notes = "단일 메뉴가 아닐때의 옵션에 따른 가격 리스트")
    private List<OptionPrice> option_prices;

    @Size(min = 1, message = "category_ids의 길이는 1 이상입니다.")
    @NotNull(message = "category_ids는 필수입니다.")
    @ApiModelProperty(notes = "선택된 카테고리 고유 id 리스트", example = "[1, 2]")
    private List<Integer> category_ids;

    @Size(max = 80, message = "description의 길이는 80 이하입니다.")
    @ApiModelProperty(notes = "메뉴 구성 설명", example = "저희 가게의 대표 메뉴 짜장면입니다.")
    private String description;

    @Size(max = 3, message = "image_urls의 size는 최대 3입니다.")
    @ApiModelProperty(notes = "이미지 URL 리스트")
    private List<String> image_urls = new LinkedList<>();

    public boolean existOfOptionDuplicate() throws Exception {
        if (this.option_prices == null || this.option_prices.isEmpty() || this.option_prices.size() == 1) {
            return false;
        }

        for (int i = 0; i < this.option_prices.size() - 1; i++) {
            String prevOption = this.option_prices.get(i).getOption();

            for (int j = i + 1; j < this.option_prices.size(); j++) {
                String nextOption = this.option_prices.get(j).getOption();

                if (prevOption.equals(nextOption)) {
                    return true;
                }
            }
        }

        return false;
    }
}
