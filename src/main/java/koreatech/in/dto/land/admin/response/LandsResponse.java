package koreatech.in.dto.land.admin.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class LandsResponse {
    private Integer total_count;
    private Integer current_count;
    private Integer total_page;
    private Integer current_page;
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
