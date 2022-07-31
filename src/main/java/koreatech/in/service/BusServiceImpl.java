package koreatech.in.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import koreatech.in.domain.Bus.SchoolBusCourse;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.BusMapper;
import koreatech.in.schedule.BusTago;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class BusServiceImpl implements BusService {
    @Autowired
    private BusMapper busMapper;

    @Autowired
    private BusTago tago;

    private static final String SCHOOL_BUS_TIMETABLE_CACHE_KEY = "Tago@busTimetable.%s.%s";

    private static final Gson gson = new Gson();

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    public static class sortByArrtime implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> a, Map<String, Object> b) {
            int aArrtime = Integer.parseInt(a.get("arrtime").toString());
            int bArrtime = Integer.parseInt(b.get("arrtime").toString());
            if (aArrtime == bArrtime) return 0;
            return aArrtime < bArrtime ? -1 : 1;
        }
    }

    @Override
    public Map<String, Object> getBus(String depart, String arrival) throws Exception {
        String target;
        List<Map<String, Object>> result;
        Map<String, Object> response = new HashMap<String, Object>();

        if (depart.equals("koreatech") && (arrival.equals("station") || arrival.equals("terminal"))) {
            target = depart;
        } else if (depart.equals("terminal") && (arrival.equals("station") || arrival.equals("koreatech"))) {
            target = depart;
        } else if (depart.equals("station") && arrival.equals("terminal")) {
            target = depart + '-' + arrival;
        } else if (depart.equals("station") && arrival.equals("koreatech")) {
            target = depart + '-' + arrival;
        } else {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 1));
        }
        for (List<String> param : BusTago.nodeIds) {
            if (!param.get(1).equals(target)) continue;
            result = tago.getBusArrivalInfo(param.get(0));
            //버스 정보가 없을 경우
            if (result.isEmpty()) return response;

            List<Map<String, Object>> tempResult = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> info : result) {
                if (IntStream.of(tago.availableBus).anyMatch(x -> x == Integer.parseInt(info.get("routeno").toString()))) {
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

    @Override
    public ArrayList<SchoolBusCourse> getCourses() {
        return busMapper.getCourses();
    }

    @Override
    public String getTimetable(String busType, String region) {
        if (!StringUtils.hasText(busType) || (!"express".equals(busType) && !StringUtils.hasText(region))) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        String redisKey = String.format(SCHOOL_BUS_TIMETABLE_CACHE_KEY, busType, "express".equals(busType) ? "" : region);
        final JsonElement[] jsonElement = new JsonElement[]{new JsonObject()};
        try {
            jsonElement[0] = gson.toJsonTree(Optional.ofNullable(stringRedisUtilStr.getDataAsString(redisKey))
                    .orElseThrow(() -> new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0))));
        } catch (IOException e) {
            throw new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0));
        }

        return jsonElement[0].getAsString();
    }
}
