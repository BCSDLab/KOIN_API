package koreatech.in.dto.admin.user.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel("AdminUserResponse")
public class UserResponse {

    @ApiModelProperty(value = "고유 id", example = "1", required = true)
    private Integer id;

    @ApiModelProperty(value = "닉네임 " + "(50자 이내)", example = "bbo")
    private String nickname;

    @ApiModelProperty(value = "이름 " + "(50자 이내)", example = "정보혁")
    private String name;

    @ApiModelProperty(value = "휴대폰 번호", example = "010-0000-0000")
    private String phone_number;

    @ApiModelProperty(example = "STUDENT", required = true)
    private UserType user_type;

    @ApiModelProperty(value = "이메일", example = "string", required = true)
    private String email;

    @ApiModelProperty(value = "성별(남:0, 여:1)", example = "1")
    private Integer gender;

    @ApiModelProperty(value = "인증 여부", example = "true", required = true)
    private Boolean is_authed;

    @ApiModelProperty(value = "최근 로그인 날짜", example = "1680585195000")
    private Date last_logged_at;

    @ApiModelProperty(value = "회원가입 일자", example = "1511885648000", required = true)
    private Date created_at;

    @ApiModelProperty(value = "업데이트 일자", example = "1680585195000", required = true)
    private Date updated_at;
}