package koreatech.in.dto.normal.user.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotNull(message = "이메일은 필수입니다.")
    @ApiModelProperty(notes = "이메일 주소 \n"
            + "- not null \n"
            + "- 이메일 형식이어야 함"
            , required = true
            , example = "abcd@gmail.com"
    )
    private String email;

    @NotNull(message = "비밀번호는 필수입니다.")
    @ApiModelProperty(notes = "비밀번호", required = true)
    private String password;
}
