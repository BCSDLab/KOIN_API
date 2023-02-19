package koreatech.in.dto.normal.user.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.student.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Deprecated // UserUpdateRequest , StudentUpdateRequest 등의 활용을 권장.
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateUserRequest {
    @ApiModelProperty(notes = "비밀번호", example = "a0240120305812krlakdsflsa;1235")
    private String password;
    private String name;
    @ApiModelProperty(notes = "성별(남:0, 여:1)", example = "0")
    private Integer gender;
    private Integer identity;
    @ApiModelProperty(notes = "졸업 여부", example = "false")
    private Boolean isGraduated;
    @ApiModelProperty(notes = "기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부", example = "컴퓨터공학부")
    private String major;
    @Size(max = 50, message = "학번은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "학번", example = "2013136000")
    private String studentNumber;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "휴대폰 번호", example = "010-0000-0000")
    private String phoneNumber;

    public Student toEntity(){
        return Student.builder()
                .password(password)
                .name(name)
                .gender(gender)
                .identity(identity)
                .isGraduated(isGraduated)
                .major(major)
                .studentNumber(studentNumber)
                .phoneNumber(phoneNumber)
                .build();
    }
}
