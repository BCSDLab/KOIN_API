package koreatech.in.dto.normal.user.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class UsersResponse {
    private Integer total_count;
    private Integer current_count;
    private Integer total_page;
    private Integer current_page;
    private List<User> users;

    @Getter
    public static final class User {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "포탈 아이디", example = "asdf1234", required = true)
        private String portal_account;

        @ApiModelProperty(notes = "닉네임", example = "1")
        private String nickname;

        @ApiModelProperty(notes = "이름", example = "김철수")
        private String name;

        @ApiModelProperty(notes = "전공", example = "컴퓨터공학부")
        private String major;

        @ApiModelProperty(notes = "학번", example = "2019136001")
        private String student_number;

        @ApiModelProperty(notes = "삭제(soft delete) 여부", example = "false", required = true)
        private Boolean is_deleted;
    }
}
