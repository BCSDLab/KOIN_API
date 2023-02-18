package koreatech.in.dto.admin.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @ApiModel("AdminLoginRequest")
public class LoginRequest {
    // TODO 23.02.12. Admin의 이메일을 이용가능한 동아리 내 메일로 변경하기.
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
