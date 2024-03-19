package koreatech.in.dto.normal.bus;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import koreatech.in.domain.Bus.BusTimetable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(SnakeCaseStrategy.class)
public class BusTimetableResponse {
    private List<? extends BusTimetable> busTimetables;

    private Date updatedAt;
}
