package koreatech.in.dto.admin.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class LoginRequest {
    @NotNull(message = "아이디는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "아이디 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "아이디", required = true)
    protected String account;

    @NotNull
    @ApiModelProperty(notes = "비밀번호", required = true)
    protected String password;
}
