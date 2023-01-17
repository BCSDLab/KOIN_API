package koreatech.in.domain.User.student;

import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.UserType;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
public class Student extends User {
    private String anonymous_nickname;
    private String student_number;
    private String major;

    private Integer identity; // 신원(0: 학생, 1: 대학원생, 2: 교수, 3: 교직원, 4: 졸업생, 5: 점주)
    private Boolean is_graduated;

    public boolean isStudentNumberValidated(){
        if(identity != null && student_number != null){
            return UserCode.isValidatedStudentNumber(identity, student_number);

        } else{
            return false;
        }
    }

    public boolean isMajorValidated() {
        if (major != null) {
            return UserCode.isValidatedDeptNumber(this.major);

        } else {
            return false;
        }
    }

    public void changeIdentity(Integer identity){
        this.identity = identity;
    }

    public void update(Student student){
        super.update(student);
        if(student.anonymous_nickname != null) {
            this.anonymous_nickname = student.getAnonymous_nickname();
        }
        if(student.student_number != null) {
            this.student_number = student.getStudent_number();
        }
        if(student.major != null) {
            this.major = student.major;
        }
        if(student.identity != null) {
            this.identity = student.identity;
        }
        if(student.is_graduated != null) {
            this.is_graduated = student.is_graduated;
        }
    }

    @Builder
    public Student(String account, String password, String email, String name, String nickname, String anonymousNickname, Integer gender, Integer identity, Boolean isGraduated, String major, String studentNumber, String phoneNumber){
        super(account, password, nickname, name, phoneNumber, email, gender, UserType.STUDENT);
        this.identity = identity;
        this.is_graduated = isGraduated;
        this.major = major;
        this.student_number = studentNumber;
        this.anonymous_nickname = anonymousNickname;
    }
}
