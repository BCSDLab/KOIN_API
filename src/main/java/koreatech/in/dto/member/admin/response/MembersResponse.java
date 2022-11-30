package koreatech.in.dto.member.admin.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class MembersResponse {
    private Integer total_count;
    private Integer current_count;
    private Integer total_page;
    private Integer current_page;
    private List<Member> members;

    @Getter
    public static final class Member {
        private Integer id;
        private String name;
        private String student_number;
        private String track;
        private String position;
        private String email;
    }
}
