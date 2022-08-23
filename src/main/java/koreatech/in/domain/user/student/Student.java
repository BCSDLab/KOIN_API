package koreatech.in.domain.user.student;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.user.User;
import koreatech.in.domain.user.UserCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Size;
import java.util.Collection;

@Getter
@Setter
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
    public String getPassword() {
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

    public boolean isStudentNumberValidated() {
        if(this.identity != null && this.studentNumber != null) {
            return UserCode.isValidatedStudentNumber(this.identity, this.studentNumber);

        } else {
            return false;
        }
    }

    public boolean isStudentMajorValidated() {
        if (this.major != null) {
            return UserCode.isValidatedDeptNumber(this.major);

        } else {
            return false;
        }
    }
}
