package koreatech.in.domain.Bus;

import koreatech.in.schedule.BusTago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component("cityBus")
public class CityBus implements Bus {
    @Autowired
    private BusTago tago;

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String target, String depart, String arrival) {
        BusRemainTime response = new BusRemainTime();
        for (List<String> param : BusTago.nodeIds) {
            if (!param.get(1).equals(target)) continue;
            List<Map<String, Object>> result = tago.getBusArrivalInfo(param.get(0));
            //버스 정보가 없을 경우
            if (result.isEmpty()) return response;

            List<Map<String, Object>> tempResult = new ArrayList<>();
            for (Map<String, Object> info : result) {
                if (IntStream.of(tago.availableBus).anyMatch(x -> x == Integer.parseInt(info.get("routeno").toString()))) {
                    tempResult.add(info);
                }
            }
            result = tempResult;
            result.sort(new SortByArrtime());

            if (result.size() == 1) {
                return new BusRemainTime.Builder()
                        .NowRemainTime(new BusRemainTime.RemainTime(Integer.parseInt(result.get(0).get("routeno").toString()), Integer.parseInt(result.get(0).get("arrtime").toString())))
                        .build();
            }
            else if (result.size() > 1) {
                return new BusRemainTime.Builder()
                        .NowRemainTime(new BusRemainTime.RemainTime(Integer.parseInt(result.get(0).get("routeno").toString()), Integer.parseInt(result.get(0).get("arrtime").toString())))
                        .NextRemainTime(new BusRemainTime.RemainTime(Integer.parseInt(result.get(1).get("routeno").toString()), Integer.parseInt(result.get(1).get("arrtime").toString())))
                        .build();
            }
            break;
        }
        return response;
    }
}
