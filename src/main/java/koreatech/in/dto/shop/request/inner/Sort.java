package koreatech.in.dto.shop.request.inner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class Sort {
    @NotNull(message = "sort의 criterion은 필수입니다.")
    @ApiModelProperty(notes = "정렬 기준", example = "NAME")
    private Criterion criterion;

    @NotNull(message = "sort의 ascending은 필수입니다.")
    @ApiModelProperty(notes = "오름차순 여부 (true: 오름차순, false: 내림차순)", example = "true")
    private Boolean ascending; // true: 오름차순, false: 내림차순

    @Getter
    private enum Criterion {
        NAME(1),
        CREATED_AT(2);

        Criterion(int value) {
            this.value = value;
        }

        private final int value;
    }
}
