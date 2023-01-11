package koreatech.in.domain.User.student;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.UserType;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Size;
import java.util.Collection;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Student extends User {
    @ApiModelProperty(notes = "익명 닉네임", example = "익명_1522771686642")
    private String anonymousNickname;
    @Size(max = 50, message = "학번은 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "학번", example = "2013136000")
    private String studentNumber;
    @ApiModelProperty(notes = "기계공학부, 컴퓨터공학부, 메카트로닉스공학부, 전기전자통신공학부, 디자인건축공학부, 에너지신소재화학공학부, 산업경영학부", example = "컴퓨터공학부")
    protected String major;
    @ApiModelProperty(notes = "신원(0: 학생, 1: 대학원생, 2: 교수, 3: 교직원, 4: 졸업생, 5: 점주)", example = "0")
    protected Integer identity;
    @ApiModelProperty(notes = "졸업 여부", example = "false")
    private Boolean isGraduated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public boolean isStudentNumberValidated(){
        if(identity != null && studentNumber != null){
            return UserCode.isValidatedStudentNumber(identity, studentNumber);

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
        if(student.anonymousNickname != null) {
            this.anonymousNickname = student.getAnonymousNickname();
        }
        if(student.studentNumber != null) {
            this.studentNumber = student.getStudentNumber();
        }
        if(student.major != null) {
            this.major = student.major;
        }
        if(student.identity != null) {
            this.identity = student.identity;
        }
        if(student.isGraduated != null) {
            this.isGraduated = student.isGraduated;
        }
    }

    @Builder
    public Student(String account, String password, String email, String name, String nickname, String anonymousNickname, Integer gender, Integer identity, Boolean isGraduated, String major, String studentNumber, String phoneNumber){
        super(account, password, nickname, name, phoneNumber, email, gender, UserType.STUDENT);
        this.identity = identity;
        this.isGraduated = isGraduated;
        this.major = major;
        this.studentNumber = studentNumber;
        this.anonymousNickname = anonymousNickname;
    }
}
