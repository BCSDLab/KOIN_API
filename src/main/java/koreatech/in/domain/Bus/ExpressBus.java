package koreatech.in.domain.Bus;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component("expressBus")
public class ExpressBus implements Bus {
    private static final String SCHOOL_BUS_TIMETABLE_CACHE_KEY = "Tago@busTimetable.%s.%s";

    private static final Gson gson = new Gson();

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String target, String depart, String arrival) {
        JsonElement expressTimetable = getTimeTable();
        JsonArray jsonArray;
        if ("koreatech".equals(depart) && "terminal".equals(arrival)) {
            jsonArray = expressTimetable.getAsJsonObject().getAsJsonArray("koreatech_to_terminal");
        } else if ("terminal".equals(depart) && "koreatech".equals(arrival)) {
            jsonArray = expressTimetable.getAsJsonObject().getAsJsonArray("terminal_to_koreatech");
        } else {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        final ZoneId tz = ZoneId.of("Asia/Seoul");
        ZonedDateTime nowZonedDateTime = ZonedDateTime.now(tz);
        LocalDate nowLocalDate = LocalDate.now();
        int nowBusIndex = 0;
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement time = jsonArray.get(i);
            String departureTimeStr = time.getAsJsonObject().getAsJsonPrimitive("departure").getAsString();
            ZonedDateTime departureTime = LocalTime.parse(departureTimeStr).atDate(nowLocalDate).atZone(tz);

            String arrivalTimeStr = time.getAsJsonObject().getAsJsonPrimitive("arrival").getAsString();
            ZonedDateTime arrivalTime = LocalTime.parse(arrivalTimeStr).atDate(nowLocalDate).atZone(tz);

            if (nowZonedDateTime.isAfter(departureTime)) {
                nowBusIndex = (i + 1) % jsonArray.size();
                if (nowZonedDateTime.isBefore(arrivalTime)) break;
                continue;
            }
            break;
        }

        JsonObject nowBusTime = jsonArray.get(nowBusIndex).getAsJsonObject();
        ZonedDateTime nowDepartureTime = LocalTime.parse(nowBusTime.getAsJsonPrimitive("departure").getAsString()).atDate(nowLocalDate).atZone(tz);

        JsonObject nextBusTime = jsonArray.get((nowBusIndex + 1) % jsonArray.size()).getAsJsonObject();
        ZonedDateTime nextDepartureTime = LocalTime.parse(nextBusTime.getAsJsonPrimitive("departure").getAsString()).atDate(nowLocalDate).atZone(tz);

        return new BusRemainTime.Builder()
                .NowRemainTime(
                        new BusRemainTime.RemainTime(null, (int) (nowDepartureTime.toEpochSecond() < nowZonedDateTime.toEpochSecond() ?
                                nowDepartureTime.plusDays(1).toEpochSecond() - nowZonedDateTime.toEpochSecond() : nowDepartureTime.toEpochSecond() - nowZonedDateTime.toEpochSecond()))
                )
                .NextRemainTime(
                        new BusRemainTime.RemainTime(null, (int) (nextDepartureTime.toEpochSecond() < nowZonedDateTime.toEpochSecond() ?
                                nextDepartureTime.plusDays(1).toEpochSecond() - nowZonedDateTime.toEpochSecond() : nextDepartureTime.toEpochSecond() - nowZonedDateTime.toEpochSecond()))
                )
                .build();
    }

    private JsonElement getTimeTable() {
        String redisKey = String.format(SCHOOL_BUS_TIMETABLE_CACHE_KEY, "express", "");
        final JsonElement[] jsonElement = new JsonElement[]{new JsonObject()};
        try {
            jsonElement[0] = gson.fromJson(Optional.ofNullable(stringRedisUtilStr.getDataAsString(redisKey))
                    .orElseThrow(() ->
                            new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0))), JsonElement.class
            );
        } catch (IOException e) {
            throw new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0));
        }
        return jsonElement[0];
    }
}
