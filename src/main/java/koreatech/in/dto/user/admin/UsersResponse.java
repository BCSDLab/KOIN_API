package koreatech.in.dto.user.admin;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class UsersResponse {
    private Integer totalCount;
    private Integer currentCount;
    private Integer totalPage;
    private Integer currentPage;
    private List<User> users;

    @Getter
    public static final class User {
        private Integer id;
        private String portal_account;
        private String nickname;
        private String name;
        private String major;
        private String student_number;
    }
}
