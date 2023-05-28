package koreatech.in.dto.admin.user.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserUpdateRequest {

    @ApiModelProperty(notes = "비밀번호", example = "a0240120305812krlakdsflsa;1235")
    private String password;

    @Size(max = 50, message = "닉네임의 길이는 최대 50자 입니다")
    @ApiModelProperty(notes = "닉네임", example = "사장님 닉네임")
    private String nickname;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @Size(max = 20, message = "휴대전화의 길이는 최대 20자 입니다")
    @ApiModelProperty(notes = "휴대전화", example = "010-0000-0000")
    private String phone_number;

    @Size(max = 50, message = "이름의 길이는 최대 50자 입니다")
    @ApiModelProperty(notes = "이름", example = "정보혁")
    private String name;

    @Size(max = 100, message = "이메일의 길이는 최대 100자입니다")
    @ApiModelProperty(notes = "이메일 \n" + "-최대 100자", example = "koin123@koreatech.ac.kr")
    private String email;

    @ApiModelProperty(notes = "성별")
    private Integer gender;
}
