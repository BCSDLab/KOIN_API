package koreatech.in.domain.Bus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SchoolBusTimetable extends BusTimetable {

    private String route_name;

    @JsonIgnore
    List<Integer> running_days;

    List<ArrivalNode> arrival_info;

    @Getter
    @ToString
    public static class ArrivalNode {

        private String node_name;

        private String arrival_time;
    }
}