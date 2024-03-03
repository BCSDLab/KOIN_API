package koreatech.in.dto.admin.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.mapstruct.admin.user.OwnerConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class NewOwnersResponse {
    @ApiModelProperty(notes = "조건에 해당하는 총 사장님의 수", example = "57", required = true)
    private Integer total_count;

    @ApiModelProperty(notes = "조건에 해당하는 사장님중에 현재 페이지에서 조회된 수", example = "10", required = true)
    private Integer current_count;

    @ApiModelProperty(notes = "조건에 해당하는 사장님들을 조회할 수 있는 최대 페이지", example = "6", required = true)
    private Integer total_page;

    @ApiModelProperty(notes = "현재 페이지", example = "2", required = true)
    private Integer current_page;

    @ApiModelProperty(notes = "사장님 리스트", required = true)
    private List<NewOwner> owners;

    @Getter @Builder
    public static class NewOwner {
        @ApiModelProperty(notes = "고유 id", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이메일", required = true)
        private String email;

        @ApiModelProperty(notes = "이름")
        private String name;

        @ApiModelProperty(notes = "전화번호")
        private String phone_number;

        @ApiModelProperty(notes = "요청한 상점ID")
        private String shop_id;

        @ApiModelProperty(notes = "요청한 상점명")
        private String shop_name;

        @ApiModelProperty(notes = "가입 신청 일자", example = "2023-01-01 12:01:02", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date created_at;
    }
}
