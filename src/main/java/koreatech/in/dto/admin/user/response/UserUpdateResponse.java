package koreatech.in.dto.admin.user.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@SuperBuilder
public class UserUpdateResponse {

    @ApiModelProperty(notes = "이메일 주소 \n"
            , required = true
            , example = "koin123@koreatech.ac.kr"
    )
    private String email;

    @ApiModelProperty(notes = "이름 \n"
            , example = "정보혁"
    )
    private String name;

    @ApiModelProperty(notes = "닉네임", example = "사장님 닉네임")
    private String nickname;

    @ApiModelProperty(notes = "휴대전화", example = "010-0000-0000")
    private String phone_number;

    @ApiModelProperty(notes = "성별")
    private Integer gender;
}
