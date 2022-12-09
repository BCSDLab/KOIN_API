package koreatech.in.dto.land.admin.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Builder
public class LandsResponse {
    @ApiModelProperty(notes = "조건에 해당하는 총 집의 수", example = "57", required = true)
    private Integer total_count;

    @ApiModelProperty(notes = "조건에 해당하는 집 중에 현재 페이지에서 조회된 수", example = "10", required = true)
    private Integer current_count;

    @ApiModelProperty(notes = "조건에 해당하는 집들을 조회할 수 있는 최대 페이지", example = "6", required = true)
    private Integer total_page;

    @ApiModelProperty(notes = "현재 페이지", example = "2", required = true)
    private Integer current_page;

    @ApiModelProperty(notes = "집 정보 리스트", required = true)
    private List<Land> lands;

    @Getter @Setter
    public static final class Land {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "금실타운", required = true)
        private String name;

        @ApiModelProperty(notes = "종류", example = "원룸")
        private String room_type;

        @ApiModelProperty(notes = "월세", example = "200만원 (6개월)")
        private String monthly_fee;

        @ApiModelProperty(notes = "전세", example = "3500")
        private String charter_fee;
    }
}
