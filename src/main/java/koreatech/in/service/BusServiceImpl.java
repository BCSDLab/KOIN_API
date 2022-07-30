package koreatech.in.service;

import com.google.gson.*;
import koreatech.in.domain.Bus.Course;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.BusMapper;
import koreatech.in.schedule.BusTago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class BusServiceImpl implements BusService {
    @Autowired
    private BusMapper busMapper;

    @Autowired
    private BusTago tago;

    private static final String BUS_SCHEDULE_CACHE_KEY = "Tago@busSchedule.%s.%s";

    private static final Gson gson = new Gson();

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> strRedisTemplate;

    private ValueOperations<String, String> strValueOps = null;

    @PostConstruct
    void init() {
        strValueOps = strRedisTemplate.opsForValue();
    }

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
            throw new PreconditionFailedException(new ErrorMessage("invalid depart or arrival", 1));
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
    public ArrayList<Course> getCourses() {
        return busMapper.getCourses();
    }

    @Override
    public String getSchedule(String busType, String region) {
        if (!StringUtils.hasText(busType) || (!"express".equals(busType) && !StringUtils.hasText(region))) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        String redisKey = String.format(BUS_SCHEDULE_CACHE_KEY, busType, "express".equals(busType) ? "" : region);
        final JsonElement[] jsonElement = new JsonElement[]{new JsonObject()};
        jsonElement[0] = gson.toJsonTree(Optional.ofNullable(strValueOps.get(redisKey))
                .orElseThrow(() -> new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0))));

        return jsonElement[0].getAsString();
    }
}
