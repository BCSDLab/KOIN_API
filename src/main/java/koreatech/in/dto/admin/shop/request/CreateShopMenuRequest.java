package koreatech.in.dto.admin.shop.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.exception.BaseException;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static koreatech.in.exception.ExceptionInformation.REQUEST_DATA_INVALID;

@Getter @Setter
public class CreateShopMenuRequest {
    @Size(min = 1, max = 25, message = "name은 1자 이상 25자 이하입니다.")
    @NotNull(message = "name은 필수입니다.")
    @ApiModelProperty(notes = "메뉴명 \n" +
                              "- not null \n" +
                              "- 1자 이상 25자 이하", example = "짜장면", required = true)
    private String name;

    @NotNull(message = "is_single은 필수입니다.")
    @ApiModelProperty(notes = "단일 메뉴 여부 \n" +
                              "- not null", example = "true", required = true)
    private Boolean is_single;

    @PositiveOrZero(message = "single_price는 0 이상 2147483647 이하입니다.")
    @ApiModelProperty(notes = "단일 메뉴일때의 가격 \n" +
                              "- is_single이 true이면 \n" +
                              "  - not null \n" +
                              "- is_single이 false이면 \n" +
                              "  - null 가능 \n" +
                              "  - null이 아닌 값이어도, 사용되지 않음 \n")
    private Integer single_price;

    @Valid
    @ApiModelProperty(notes = "단일 메뉴가 아닐때의 옵션에 따른 가격 리스트 \n" +
                              "- is_single이 true이면 \n" +
                              "  - null 가능 \n" +
                              "  - null이 아닌 값이어도, 사용되지 않음 \n" +
                              "- is_single이 false이면 \n" +
                              "  - not null \n" +
                              "  - 리스트의 최소 길이: 1")
    private List<OptionPrice> option_prices = new ArrayList<>();

    @NotEmpty(message = "category_ids는 필수이며, 리스트의 최소 길이는 1입니다.")
    @ApiModelProperty(notes = "선택된 카테고리 고유 id 리스트 \n" +
                              "- not null \n" +
                              "- 리스트의 최소 길이: 1", required = true)
    private List<Integer> category_ids = new ArrayList<>();

    @Size(max = 80, message = "description의 길이는 80 이하여야합니다.")
    @ApiModelProperty(notes = "메뉴 구성 설명 \n" +
                              "- 최대 80자", example = "저희 가게의 대표 메뉴 짜장면입니다.")
    private String description;

    @Size(max = 3, message = "image_urls의 size는 최대 3입니다.")
    @ApiModelProperty(notes = "이미지 URL 리스트 \n" +
                              "- 리스트의 최대 길이: 3")
    private List<String> image_urls = new ArrayList<>();

    @Getter @Setter
    @ApiModel("OptionPrice_3")
    public static class OptionPrice {
        @Size(min = 1, max = 50, message = "option_prices의 option은 1자 이상 50자 이하입니다.")
        @NotNull(message = "option_prices의 option은 필수입니다.")
        @ApiModelProperty(notes = "옵션명 \n" +
                                  "- not null \n" +
                                  "- 1자 이상 50자 이하", example = "곱빼기", required = true)
        private String option;

        @PositiveOrZero(message = "option_prices의 price는 0 이상 2147483647 이하입니다.")
        @NotNull(message = "option_prices의 price는 필수입니다.")
        @ApiModelProperty(notes = "옵션에 대한 가격 \n" +
                                  "- not null \n" +
                                  "- 0 이상 2147483647 이하", example = "12000", required = true)
        private Integer price;
    }

    public void checkDataConstraintViolation() {
        checkPriceConstraintViolation();
        checkDuplicationOfOptions();
    }

    private void checkPriceConstraintViolation() {
        if (isSingleMenu() && isSinglePriceNull()) {
            throw new BaseException("is_single이 true이면 single_price는 필수입니다.", REQUEST_DATA_INVALID);
        }
        if (!isSingleMenu() && isOptionPricesEmpty()) {
            throw new BaseException("is_single이 false이면 option_prices는 필수이며, 리스트의 최소 길이는 1입니다.", REQUEST_DATA_INVALID);
        }
    }

    private void checkDuplicationOfOptions() {
        if (isOptionPricesEmpty() || getOptionPricesSize() == 1) {
            return;
        }

        Set<String> optionSet = this.option_prices.stream()
                .map(OptionPrice::getOption)
                .collect(Collectors.toSet());

        // 요청된 개수와 Set에 담긴 개수가 다르면 옵션명의 중복이 있다는 것
        if (getOptionPricesSize() != optionSet.size()) {
            throw new BaseException("중복되는 option이 존재합니다.", REQUEST_DATA_INVALID);
        }
    }

    public boolean isSingleMenu() {
        return this.is_single;
    }

    public boolean isImageUrlsExist() {
        return !this.image_urls.isEmpty();
    }

    private boolean isSinglePriceNull() {
        return this.single_price == null;
    }

    private boolean isOptionPricesEmpty() {
        return this.option_prices.isEmpty();
    }

    private int getOptionPricesSize() {
        return this.option_prices.size();
    }
}
