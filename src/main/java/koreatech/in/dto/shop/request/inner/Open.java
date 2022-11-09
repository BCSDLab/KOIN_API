package koreatech.in.dto.shop.request.inner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.DayOfWeek;

@Getter @Setter
public class Open {
    @NotNull(message = "open의 day_of_week는 필수입니다.")
    @ApiModelProperty(notes = "요일", example = "MONDAY")
    private DayOfWeek day_of_week;

    @NotNull(message = "open의 closed는 필수입니다.")
    @ApiModelProperty(notes = "휴무 여부", example = "false")
    private Boolean closed;

    @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$", message = "open의 open_time은 시간 형식입니다.")
    @ApiModelProperty(notes = "여는 시간", example = "10:00")
    private String open_time;

    @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$", message = "open의 closed_time은 시간 형식입니다.")
    @ApiModelProperty(notes = "닫는 시간", example = "22:30")
    private String close_time;
}
