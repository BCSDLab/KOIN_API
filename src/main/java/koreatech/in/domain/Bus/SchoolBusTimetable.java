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
    private List<String> running_days;

    private List<ArrivalNode> arrival_info;

    @Getter
    @ToString
    public static class ArrivalNode implements Comparable<ArrivalNode> {

        private String node_name;

        private String arrival_time;

        @Override
        public int compareTo(ArrivalNode o) {
            return arrival_time.compareTo(o.arrival_time);
        }
    }
}