package koreatech.in.dto.normal.user.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSubTypes({
        @JsonSubTypes.Type(value = OwnerRegisterRequest.class)
})

public class UserRegisterRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotNull(message = "이메일은 필수입니다.")
    @ApiModelProperty(notes = "이메일 주소 \n"
            + "- not null \n"
            + "- 이메일 형식이어야 함"
            , required = true
            , example = "koin123@koreatech.ac.kr"
    )
    private String email;

    @Size(max = 50, message = "이름은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "이름 \n"
            + "50자 이내여야 함"
            , example = "정보혁"
    )
    private String name;


    @NotNull(message = "비밀번호는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "비밀번호 \n"
            + "- not null"
            , required = true
            , example = "a0240120305812krlakdsflsa;1235"
    )
    private String password;
}
