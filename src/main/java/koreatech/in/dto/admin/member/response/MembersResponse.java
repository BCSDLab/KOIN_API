package koreatech.in.dto.admin.member.response;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.mapstruct.admin.AdminMemberConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class MembersResponse {
    @ApiModelProperty(notes = "조건에 해당하는 총 회원의 수", example = "57", required = true)
    private Integer total_count;

    @ApiModelProperty(notes = "조건에 해당하는 회원 중에 현재 페이지에서 조회된 수", example = "10", required = true)
    private Integer current_count;

    @ApiModelProperty(notes = "조건에 해당하는 회원들을 조회할 수 있는 최대 페이지", example = "6", required = true)
    private Integer total_page;

    @ApiModelProperty(notes = "현재 페이지", example = "2", required = true)
    private Integer current_page;

    @ApiModelProperty(notes = "회원 정보 리스트", required = true)
    private List<Member> members;

    @Getter @Builder
    public static final class Member {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "김주원", required = true)
        private String name;

        @ApiModelProperty(notes = "학번", example = "2019136037")
        private String student_number;

        @ApiModelProperty(notes = "트랙 이름", example = "BackEnd", required = true)
        private String track;

        @ApiModelProperty(notes = "직책", example = "Regular", required = true)
        private String position;

        @ApiModelProperty(notes = "이메일", example = "damiano102777@naver.com")
        private String email;

        @ApiModelProperty(notes = "이미지 url", example = "https://static.koreatech.in/test.png")
        private String image_url;

        @ApiModelProperty(notes = "삭제(soft delete) 여부", example = "false", required = true)
        private Boolean is_deleted;
    }

    public static MembersResponse of(Integer totalCount, Integer totalPage, Integer currentPage, List<koreatech.in.domain.Homepage.Member> members) {
        return MembersResponse.builder()
                .total_count(totalCount)
                .current_count(members.size())
                .total_page(totalPage)
                .current_page(currentPage)
                .members(
                        members.stream()
                                .map(AdminMemberConverter.INSTANCE::toMembersResponse$Member)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
