package koreatech.in.dto.normal.user.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @NotNull(message = "아이디는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "아이디 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "아이디 \n"
            + "- not null \n"
            + "아이디 형식이어야 함"
            , required = true
            , example = "jjw266"
    )
    private String account;

    @NotNull(message = "비밀번호는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "비밀번호 \n"
            + "- not null"
            , required = true
            , example = "a0240120305812krlakdsflsa;1235"
    )
    private String password;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotNull(message = "이메일은 필수입니다.")
    @ApiModelProperty(notes = "이메일 주소 \n"
            + "- not null \n"
            + "- 이메일 형식이어야 함"
            , required = true
            , example = "abcd@gmail.com"
    )
    private String email;

    @Size(max = 50, message = "이름은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "이름 \n"
            + "- not null \n"
            + "50자 이내여야 함"
            , required = true
            , example = "정보혁"
    )
    private String name;
}
