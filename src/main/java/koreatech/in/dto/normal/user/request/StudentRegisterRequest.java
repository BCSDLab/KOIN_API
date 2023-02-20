package koreatech.in.dto.normal.user.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import koreatech.in.domain.User.student.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StudentRegisterRequest extends UserRegisterRequest{
//
//    @Size(max = 50, message = "이름은 50자 이내여야 합니다.")
//    @ApiModelProperty(notes = "이름 \n"
//            + "50자 이내여야 함"
//            , required = true
//            , example = "정보혁"
//    )
//    private String name;

    @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
    @ApiModelProperty(notes = "닉네임 \n"
            + "- 10자 이내여야 함."
            , example = "bbo"
    )
    private String nickname;

    @ApiModelProperty(notes = "성별(남:0, 여:1) \n"
            + "- not null \n"
            + "- 이메일 형식이어야 함"
            , example = "1"
    )
    private Integer gender;

    @ApiModelProperty(notes = "졸업 여부"
            , example = "false"
    )
    private Boolean isGraduated;

    @ApiModelProperty(notes = "전공{기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부}",
            example = "컴퓨터공학부")
    private String major;

    @Size(max = 50, message = "학번은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "학번\n"
            + "- 50자 이내여야 함"
            , example = "2029136012"
    )
    private String studentNumber;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "휴대폰 번호\n"
            + "- 휴대폰 번호 형식이여야 함"
            ,example = "010-0000-0000"
    )
    private String phoneNumber;

    @Deprecated
    public Student toEntity(Integer identity){
        if (isGraduated == null) {
            isGraduated = false;
        }

        return Student.builder()
                .email(getEmail())
                .password(getPassword())
                .name(getName())
                .nickname(nickname)
                .gender(gender)
                .identity(identity)
                .isGraduated(isGraduated)
                .major(major)
                .studentNumber(studentNumber)
                .phoneNumber(phoneNumber)
                .build();
    }
}