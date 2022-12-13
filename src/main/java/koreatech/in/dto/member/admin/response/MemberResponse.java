package koreatech.in.dto.member.admin.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class MemberResponse {
    @ApiModelProperty(notes = "회원 정보")
    private Member member;

    @Getter @Builder
    public static final class Member {
        @ApiModelProperty(notes = "고유 id", example = "1")
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "김주원")
        private String name;

        @ApiModelProperty(notes = "학번", example = "2019136037")
        private String student_number;

        @ApiModelProperty(notes = "트랙 이름", example = "BackEnd")
        private String track;

        @ApiModelProperty(notes = "직책", example = "Regular")
        private String position;

        @ApiModelProperty(notes = "이메일", example = "damiano102777@naver.com")
        private String email;

        @ApiModelProperty(notes = "이미지 url", example = "https://static.koreatech.in/example.png")
        private String image_url;

        @ApiModelProperty(notes = "삭제(soft delete) 여부", example = "false")
        private Boolean is_deleted;
    }
}
