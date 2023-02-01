package koreatech.in.dto.normal.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class FindPasswordRequest {
    @NotNull(message = "account는 필수입니다.")
    @ApiModelProperty(notes = "아이디 \n" +
                              "- not null", example = "acsdefg1234", required = true)
    private String account;
}
