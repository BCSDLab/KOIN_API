package koreatech.in.dto.admin.user.owner.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel("AdminOwnerUpdateRequest")
public class OwnerUpdateRequest {
    @NotBlank(message = "사업자 등록 번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}", message = "사업자 등록 번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "사업자 등록 번호 \n"
            + "- not blank \n"
            + "- 사업자 등록 번호 형식이어야 함"
            , example = "012-34-56789"
            , required = true
    )
    private String company_registration_number;

    @ApiModelProperty(notes = "상점 수정 권한", example = "false")
    private Boolean grant_shop;

    @ApiModelProperty(notes = "이벤트수정권한", example = "false")
    private Boolean grant_event;

    @Size(max = 50, message = "닉네임의 길이는 최대 50자 입니다")
    @ApiModelProperty(notes = "사장님닉네임", example = "사장님 닉네임")
    private String nickname;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @Size(max = 20, message = "휴대전화의 길이는 최대 20자 입니다")
    @ApiModelProperty(notes = "사장님휴대전화", example = "010-0000-0000")
    private String phone_number;

    @Size(max = 50, message = "이름의 길이는 최대 50자 입니다")
    @ApiModelProperty(notes = "사장님성함", example = "장준영")
    private String name;

    @Size(max = 100, message = "이메일의 길이는 최대 100자입니다")
    @ApiModelProperty(notes = "이메일 \n" + "-최대 100자", example = "testmailnotvalid@gmail.com")
    private String email;

    @ApiModelProperty(notes = "사장님성별")
    private Integer gender;
}
