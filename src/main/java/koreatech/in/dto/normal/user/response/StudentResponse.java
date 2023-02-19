package koreatech.in.dto.normal.user.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import koreatech.in.domain.User.student.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class StudentResponse extends UserResponse {
    private String anonymousNickname;
    private String studentNumber;
    private String major;
    private Integer identity;
    private Boolean isGraduated;
    private String phoneNumber;

    public StudentResponse(Student student) {
        super(student);
        this.anonymousNickname = student.getAnonymous_nickname();
        this.studentNumber = student.getStudent_number();
        this.major = student.getMajor();
        this.identity = student.getIdentity();
        this.isGraduated = student.getIs_graduated();
        this.phoneNumber = student.getPhone_number();
    }
}
