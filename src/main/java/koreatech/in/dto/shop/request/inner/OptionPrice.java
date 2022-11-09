package koreatech.in.dto.shop.request.inner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter @Setter
public class OptionPrice {
    @Size(min = 1, max = 12, message = "option_prices의 option은 1자 이상 12자 이하입니다.")
    @NotNull(message = "option_prices의 option은 필수입니다.")
    @ApiModelProperty(notes = "옵션명", example = "곱빼기")
    private String option;

    @PositiveOrZero(message = "option_prices의 price는 0 이상 2147483647 이하입니다.")
    @Max(value = Integer.MAX_VALUE, message = "option_prices의 price는 0 이상 2147483647 이하입니다.")
    @NotNull(message = "option_prices의 price는 필수입니다.")
    @ApiModelProperty(notes = "옵션에 대한 가격", example = "12000")
    private Integer price;
}
