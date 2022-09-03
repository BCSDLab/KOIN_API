package koreatech.in.controller.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import koreatech.in.controller.user.dto.UserResponse;
import koreatech.in.domain.user.UserType;
import koreatech.in.domain.user.student.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StudentResponse extends UserResponse {
    private String anonymousNickname;
    private String studentNumber;
    private String major;
    private Integer identity;
    private Boolean isGraduated;
    private String phoneNumber;

    public StudentResponse(Student student) {
        super(student);
        this.anonymousNickname = student.getAnonymousNickname();
        this.studentNumber = student.getStudentNumber();
        this.major = student.getMajor();
        this.identity = student.getIdentity();
        this.isGraduated = student.getIsGraduated();
        this.phoneNumber = student.getPhoneNumber();
    }
}
