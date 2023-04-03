package koreatech.in.dto.admin.user.student;

import koreatech.in.dto.admin.user.response.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class StudentResponse extends UserResponse {
    private String anonymous_nickname;
    private String student_number;
    private String major;
    private Boolean is_graduated;
}
