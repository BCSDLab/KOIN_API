package koreatech.in.dto.admin.user.student.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.admin.user.response.UserUpdateResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@ApiModel("AdminStudentUpdateResponse")
public class StudentUpdateResponse extends UserUpdateResponse {

    @ApiModelProperty(notes = "학번"
            , example = "2029136012"
    )
    private String studentNumber;

    @ApiModelProperty(notes = "전공{기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부}"
            , example = "컴퓨터공학부")
    private String major;

    @ApiModelProperty(notes = "[NOT UPDATE] 신원 (학생, 사장님, 등)"
            , example = "0")
    private Integer identity;

    @ApiModelProperty(notes = "[NOT UPDATE] 졸업 여부"
            , example = "false")
    private Boolean isGraduated;

    @ApiModelProperty(notes = "닉네임"
            , example = "bbo"
    )
    private String nickname;

    @ApiModelProperty(notes = "휴대폰 번호"
            , example = "010-0000-0000"
    )
    private String phoneNumber;

    @ApiModelProperty(notes = "성별(남:0, 여:1)"
            , example = "1"
    )
    private Integer gender;
}
