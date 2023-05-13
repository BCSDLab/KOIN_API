package koreatech.in.dto.admin.user.owner.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class OwnerUpdateRequest {
    @NotNull(message = "사업자등록번호는 필수입니다")
    @Size(max = 12, message = "사업자등록번호의 길이는 최대 12자 입니다")
    @ApiModelProperty(notes = "사업자등록번호 \n" + "- not null \n" + "- 최대 12자", example = "2019161116", required = true)
    private String company_registration_number;

    @ApiModelProperty(notes = "상점수정권한", example = "default=0")
    private Boolean grant_shop;

    @ApiModelProperty(notes = "이벤트수정권한", example = "default=0")
    private Boolean grant_event;

    @Size(max = 50, message = "닉네임의 길이는 최대 50자 입니다")
    @ApiModelProperty(notes = "사장님닉네임")
    private String nickname;

    @Size(max = 20, message = "휴대전화의 길이는 최대 20자 입니다")
    @ApiModelProperty(notes = "사장님휴대전화")
    private String phone_number;

    @Size(max = 50, message = "이름의 길이는 최대 50자 입니다")
    @ApiModelProperty(notes = "사장님성함")
    private String name;

    @NotNull(message = "이메일은 필수입니다")
    @Size(max = 100, message = "이메일의 길이는 최대 100자입니다")
    @ApiModelProperty(notes = "이메일 \n" + " -not null \n" + "-최대 100자", example = "police0022@koreatech.ac.kr", required = true)
    private String email;

    @ApiModelProperty(notes = "사장님성별")
    private Integer gender;
}
