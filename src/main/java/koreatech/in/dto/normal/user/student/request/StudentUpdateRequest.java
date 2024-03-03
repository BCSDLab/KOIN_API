package koreatech.in.dto.normal.user.student.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.normal.user.request.UserUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StudentUpdateRequest extends UserUpdateRequest {

    @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
    @ApiModelProperty(notes = "닉네임", example = "bbo")
    private String nickname;

    @ApiModelProperty(notes = "성별(남:0, 여:1)", example = "0")
    private Integer gender;

    @ApiModelProperty(notes = "[NOT UPDATE] 신원 (학생, 사장님, 등)", example = "0")
    private Integer identity;

    @ApiModelProperty(notes = "[NOT UPDATE] 졸업 여부", example = "false")
    private Boolean isGraduated;

    @ApiModelProperty(notes = "기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부", example = "컴퓨터공학부")
    private String major;

    @Size(max = 50, message = "학번은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "학번", example = "2013136000")
    private String studentNumber;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "휴대폰 번호", example = "010-0000-0000")
    private String phoneNumber;
}
