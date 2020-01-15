package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.schedule.BusTago;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class BusServiceImpl implements BusService {

    @Resource(name = "redisTemplate")
    private ValueOperations<String, List<Map<String,Object>>> valueOps;

    private BusTago tago = new BusTago();

    @Override
    public Map<String, Object> getBus (String depart, String arrival) throws Exception {
        String target;
        List<Map<String, Object>> result;
        Map<String, Object> response = new HashMap<String, Object>();

        if (depart.equals("koreatech") && (arrival.equals("station") || arrival.equals("terminal"))) {
            target=depart;
        }
        else if(depart.equals("terminal") && (arrival.equals("station") || arrival.equals("koreatech"))) {
            target=depart;
        }
        else if(depart.equals("station") && arrival.equals("terminal")) {
            target=depart +'-'+arrival;
        }
        else if(depart.equals("station") && arrival.equals("koreatech")) {
            target=depart +'-'+arrival;
        }
        else {
            throw new PreconditionFailedException(new ErrorMessage("invalid depart or arrival", 1));
        }
        for (List<String> param: BusTago.nodeIds) {
            if (!param.get(1).equals(target)) continue;
            result = tago.getBusArrivalInfo(param.get(0));

            //버스 정보가 없을 경우
            if (result.isEmpty()) return new HashMap<String, Object>();

            List<Map<String, Object>> tempResult = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> info: result) {
                if (IntStream.of(tago.avaliableBus).anyMatch(x -> x == Integer.parseInt(info.get("routeno").toString()))) {
                    tempResult.add(info);
                }
            }
            result = tempResult;
            result.sort(new sortByArrtime());

            if (result.size() == 1) {
                response.put("bus_number", Integer.parseInt(result.get(0).get("routeno").toString()));
                response.put("remain_time", Integer.parseInt(result.get(0).get("arrtime").toString()));
                response.put("next_bus_number", null);
                response.put("next_remain_time", null);
            }
            if (result.size() > 1) {
                response.put("bus_number", Integer.parseInt(result.get(0).get("routeno").toString()));
                response.put("remain_time", Integer.parseInt(result.get(0).get("arrtime").toString()));
                response.put("next_bus_number", Integer.parseInt(result.get(1).get("routeno").toString()));
                response.put("next_remain_time", Integer.parseInt(result.get(1).get("arrtime").toString()));
            }
            break;
        }
        return response;
    }

    public class sortByArrtime implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> a, Map<String, Object> b) {
            int aArrtime = Integer.parseInt(a.get("arrtime").toString());
            int bArrtime = Integer.parseInt(b.get("arrtime").toString());
            if (aArrtime == bArrtime) return 0;
            return aArrtime < bArrtime ? -1 : 1;
        }
    }
}
