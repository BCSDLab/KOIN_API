package koreatech.in.dto.land.admin.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class LandsResponse {
    private Integer totalCount;
    private Integer currentCount;
    private Integer totalPage;
    private Integer currentPage;
    private List<Land> lands;

    @Getter
    public static final class Land {
        private Integer id;
        private String name;
        private String room_type;
        private String monthly_fee;
        private String charter_fee;
    }
}
