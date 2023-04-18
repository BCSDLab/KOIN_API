package koreatech.in.dto.admin.user.student;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.admin.user.response.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel("AdminStudentResponse")
public class StudentResponse extends UserResponse {

    @ApiModelProperty(value = "익명 닉네임 " + "(255자 이내)", example = "익명_1676688416361")
    private String anonymous_nickname;

    @ApiModelProperty(value = "학번", example = "2029136012")
    private String student_number;

    @ApiModelProperty(value = "전공{기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부}",
            example = "컴퓨터공학부")
    private String major;

    @ApiModelProperty(value = "졸업X:false, 졸업O:true", example = "false")
    private Boolean is_graduated;
}
