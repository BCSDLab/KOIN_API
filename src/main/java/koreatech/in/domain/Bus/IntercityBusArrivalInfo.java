package koreatech.in.domain.Bus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IntercityBusArrivalInfo implements Comparable<IntercityBusArrivalInfo> {

    private String arrPlaceNm;

    private String arrPlandTime;

    private String depPlaceNm;

    private int charge;

    private String depPlandTime;

    private String gradeNm;

    private String routeId;

    @Override
    public int compareTo(IntercityBusArrivalInfo o) {
        return this.depPlandTime.compareTo(o.depPlandTime);
    }
}
