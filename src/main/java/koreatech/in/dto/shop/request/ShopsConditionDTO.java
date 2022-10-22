package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.shop.request.inner.Filter;
import koreatech.in.dto.shop.request.inner.Sort;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter @Setter
public class ShopsConditionDTO {
    @Positive(message = "page는 1 이상 2147483647 이하입니다.")
    @Max(value = Integer.MAX_VALUE, message = "page는 1 이상 2147483647 이하입니다.")
    @NotNull(message = "page는 필수입니다.")
    @ApiModelProperty(notes = "페이지", example = "1")
    private Integer page;

    @Positive(message = "limit는 1 이상 2147483647 이하입니다.")
    @Max(value = Integer.MAX_VALUE, message = "limit는 1 이상 2147483647 이하입니다.")
    @NotNull(message = "limit는 필수입니다.")
    @ApiModelProperty(notes = "한 페이지에 조회할 상점의 최대 개수", example = "10")
    private Integer limit;

    @Valid
    @ApiModelProperty(notes = "필터링 종류")
    private Filter filter;

    @Valid
    @ApiModelProperty(notes = "정렬 방식")
    private Sort sort;

    @Size(min = 1, message = "search_name은 1자 이상입니다.")
    @ApiModelProperty(notes = "이름 검색 문자열")
    private String search_name;

    public ShopsConditionDTO removeBlankOfSearchName() {
        if (this.search_name != null) {
            this.search_name = this.search_name.replace(" ", "").toLowerCase();
        }
        return this;
    }
}
