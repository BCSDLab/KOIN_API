package koreatech.in.dto.normal.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class LoginRequest {
    @NotNull(message = "account는 필수입니다.")
    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "account의 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "아이디", example = "damiano1027", required = true)
    private String account;

    @NotNull(message = "password는 필수입니다.")
    @ApiModelProperty(notes = "비밀번호", required = true)
    private String password;
}
