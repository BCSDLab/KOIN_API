package koreatech.in.domain.Bus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityBusArrivalInfo implements Comparable<CityBusArrivalInfo> {
    
    // 남은 정거장 개수
    private int arrprevstationcnt;

    // 도착까지 남은 시간
    private int arrtime;
    
    private String nodeid;

    private String nodenm;

    private String routeid;

    // 버스 번호
    private int routeno;

    // 버스 종류
    private String routetp;

    // 차량 종류
    private String vehicletp;

    @Override
    public int compareTo(CityBusArrivalInfo o) {
        return this.arrtime - o.arrtime;
    }
}
