package koreatech.in.dto.admin.user.owner.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@ApiModel("AdminOwnerUpdateResponse")
public class OwnerUpdateResponse {
    @ApiModelProperty(notes = "사업자등록번호" + "- 최대 12자", example = "012-34-56789")
    private String company_registration_number;

    @ApiModelProperty(notes = "상점 수정 권한", example = "false")
    private Boolean grant_shop;

    @ApiModelProperty(notes = "이벤트수정권한", example = "false")
    private Boolean grant_event;

    @ApiModelProperty(notes = "사장님닉네임", example = "사장님 닉네임")
    private String nickname;

    @ApiModelProperty(notes = "사장님휴대전화", example = "010-0000-0000")
    private String phone_number;

    @ApiModelProperty(notes = "사장님성함", example = "장준영")
    private String name;

    @ApiModelProperty(notes = "이메일" + "-최대 100자", example = "testmailnotvalid@gmail.com")
    private String email;

    @ApiModelProperty(notes = "사장님성별")
    private Integer gender;
}
