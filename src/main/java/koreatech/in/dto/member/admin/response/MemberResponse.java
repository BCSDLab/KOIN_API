package koreatech.in.dto.member.admin.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class MemberResponse {
    private Integer id;
    private String name;
    private String student_number;
    private String track;
    private String position;
    private String email;
    private String image_url;
    private Boolean is_deleted;
}
