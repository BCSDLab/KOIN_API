package koreatech.in.dto.normal.user.student.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.normal.user.response.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class StudentResponse extends UserResponse {

    @ApiModelProperty(notes = "익명 닉네임\n",
            example = "익명_1676688416361"
    )
    private String anonymousNickname;

    @ApiModelProperty(notes = "학번",
            example = "2029136012"
    )
    private String studentNumber;

    @ApiModelProperty(notes = "전공{기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부}",
            example = "컴퓨터공학부")
    private String major;

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

    public StudentResponse(Student student) {
        super(student);
        this.anonymousNickname = student.getAnonymous_nickname();
        this.studentNumber = student.getStudent_number();
        this.major = student.getMajor();
        this.nickname = student.getNickname();
        this.phoneNumber = student.getPhone_number();
        this.gender = student.getGender();
    }
}
