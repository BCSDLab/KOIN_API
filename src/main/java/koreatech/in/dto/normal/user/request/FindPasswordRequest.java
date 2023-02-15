package koreatech.in.dto.normal.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class FindPasswordRequest {
    @NotNull(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이어야 합니다.")
    @ApiModelProperty(notes = "이메일 \n" +
                              "- not null \n" +
                              "- 이메일 형식이어야 함", example = "abc123@test.com", required = true)
    private String email;
}
