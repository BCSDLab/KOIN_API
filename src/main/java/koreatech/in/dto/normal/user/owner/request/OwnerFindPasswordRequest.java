package koreatech.in.dto.normal.user.owner.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OwnerFindPasswordRequest {

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
    private String address;
}
