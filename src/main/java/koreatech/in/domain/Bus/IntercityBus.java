package koreatech.in.domain.Bus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import koreatech.in.domain.NotiSlack;
import koreatech.in.util.SlackNotiSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("intercityBus")
public class IntercityBus extends Bus {

    private static final String CACHE_KEY_BUS_ARRIVAL_INFO = "Tago@busArrivalInfo:intercityBus:%s:%s";

    private static final String CACHE_KEY_BUS_ERROR_ALERT = "Tago@busErrorAlert:%s";

    private static final Type arrivalInfoType = new TypeToken<List<IntercityBusArrivalInfo>>() {
    }.getType();

    @Autowired
    private SlackNotiSender sender;

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String depart, String arrival) {
        BusRemainTime response = new BusRemainTime();
        try {
            BusTerminalEnum departTerminal = Objects.requireNonNull(BusTerminalEnum.findByTerminalName(depart));
            BusTerminalEnum arrivalTerminal = Objects.requireNonNull(BusTerminalEnum.findByTerminalName(arrival));

            List<IntercityBusArrivalInfo> arrivalInfos = getArrivalTimes(departTerminal, arrivalTerminal);
            if (arrivalInfos.isEmpty()) {
                return response;
            }

            final ZoneId tz = ZoneId.of("Asia/Seoul");
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            ZonedDateTime nowZonedDateTime = ZonedDateTime.now(tz);
            int nowBusIndex = 0;
            for (int i = 0; i < arrivalInfos.size(); i++) {
                IntercityBusArrivalInfo arrivalInfo = arrivalInfos.get(i);
                ZonedDateTime departureTime = LocalDateTime.parse(arrivalInfo.getDepPlandTime(), dateTimeFormatter).atZone(tz);
                ZonedDateTime arrivalTime = LocalDateTime.parse(arrivalInfo.getArrPlandTime(), dateTimeFormatter).atZone(tz);

                if (nowZonedDateTime.isAfter(departureTime)) {
                    nowBusIndex = (i + 1) % arrivalInfos.size();
                    if (nowZonedDateTime.isBefore(arrivalTime)) break;
                    continue;
                }
                break;
            }

            IntercityBusArrivalInfo nowBusTime = arrivalInfos.get(nowBusIndex);
            ZonedDateTime nowDepartureTime = LocalDateTime.parse(nowBusTime.getDepPlandTime(), dateTimeFormatter).atZone(tz);

            IntercityBusArrivalInfo nextBusTime = arrivalInfos.get((nowBusIndex + 1) % arrivalInfos.size());
            ZonedDateTime nextDepartureTime = LocalDateTime.parse(nextBusTime.getDepPlandTime(), dateTimeFormatter).atZone(tz);

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
        } catch (JsonSyntaxException | NullPointerException e) {
            return response;
        }
    }

    private List<IntercityBusArrivalInfo> getArrivalTimes(@NotNull BusTerminalEnum departTerminal,
                                                          @NotNull BusTerminalEnum arrivalTerminal) {
        return Optional.ofNullable(getArrivalTimesFromCache(departTerminal, arrivalTerminal))
                .orElseGet(() -> getArrivalTimesFromReal(departTerminal, arrivalTerminal));
    }

    private List<IntercityBusArrivalInfo> getArrivalTimesFromCache(@NotNull BusTerminalEnum departTerminal,
                                                                   @NotNull BusTerminalEnum arrivalTerminal) {
        String cacheKey = getCacheKey(departTerminal.getTerminalName(), arrivalTerminal.getTerminalName());
        try {
            String cachedValue = stringRedisUtilStr.getDataAsString(cacheKey);
            return gson.fromJson(cachedValue, arrivalInfoType);
        } catch (NullPointerException | IOException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<IntercityBusArrivalInfo> getArrivalTimesFromReal(@NotNull BusTerminalEnum departTerminal,
                                                                  @NotNull BusTerminalEnum arrivalTerminal) {
        try {
            String arrivalTimes = getBusResult(departTerminal.getTerminalID(), arrivalTerminal.getTerminalID());
            List<IntercityBusArrivalInfo> arrivalInfos = extractBusArrivalInfo(arrivalTimes)
                    .stream()
                    .sorted()
                    .collect(Collectors.toList());
            cacheBusArrivalInfo(departTerminal, arrivalTerminal, arrivalInfos);

            return arrivalInfos;
        } catch (Exception e) {
            return null;
        }
    }

    private String getBusResult(String depTerminalId, String arrTerminalId) throws IOException {
        String urlBuilder = "http://apis.data.go.kr/1613000/SuburbsBusInfoService/getStrtpntAlocFndSuberbsBusInfo" + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + OPEN_API_KEY + // API Key
                "&" + URLEncoder.encode("depTerminalId", "UTF-8") + "=" + URLEncoder.encode(depTerminalId, "UTF-8") + // 출발 터미널 ID
                "&" + URLEncoder.encode("arrTerminalId", "UTF-8") + "=" + URLEncoder.encode(arrTerminalId, "UTF-8") + // 도착 터미널 ID
                "&" + URLEncoder.encode("depPlandTime", "UTF-8") + "=" + ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + // 출발 일자
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "30" +
                "&_type=json";
        return requestOpenAPI(urlBuilder);
    }

    private List<IntercityBusArrivalInfo> extractBusArrivalInfo(String response) {
        List<IntercityBusArrivalInfo> result = new ArrayList<>();
        try {
            JsonObject nodeInfo = new JsonParser().parse(response).getAsJsonObject();
            String resultCode = nodeInfo.getAsJsonObject("response").getAsJsonObject("header").getAsJsonPrimitive("resultCode").getAsString();
            if (checkOpenAPIError(resultCode)) {
                return result;
            }

            JsonObject bodyObject = nodeInfo.getAsJsonObject("response").getAsJsonObject("body");
            int count = bodyObject.get("totalCount").getAsInt();
            if (count == 0) {
                return result;
            }

            JsonElement item = bodyObject.getAsJsonObject("items").get("item");
            if (item.isJsonArray()) {
                return gson.fromJson(item, arrivalInfoType);
            } else if (item.isJsonObject()) {
                result.add(gson.fromJson(item, IntercityBusArrivalInfo.class));
            }
            return result;
        } catch (JsonSyntaxException e) {
            return result;
        }
    }

    @Override
    public void cacheBusArrivalInfo() {
        BusTerminalEnum koreatechTerminal = BusTerminalEnum.KOREATECH;
        BusTerminalEnum terminalTerminal = BusTerminalEnum.TERMINAL;
        getArrivalTimesFromReal(koreatechTerminal, terminalTerminal);
        getArrivalTimesFromReal(terminalTerminal, koreatechTerminal);
    }

    private void cacheBusArrivalInfo(@NotNull BusTerminalEnum departTerminal,
                                     @NotNull BusTerminalEnum arrivalTerminal,
                                     List<IntercityBusArrivalInfo> arrivalInfos) throws IOException {
        String cacheKey = getCacheKey(departTerminal.getTerminalName(), arrivalTerminal.getTerminalName());
        stringRedisUtilObj.setDataAsString(cacheKey, arrivalInfos);
    }

    private String getCacheKey(String depart, String arrival) {
        return String.format(CACHE_KEY_BUS_ARRIVAL_INFO, depart, arrival);
    }

    private boolean checkOpenAPIError(String resultCode) {
        if ("12".equals(resultCode)) {
            sendErrorNotice("버스도착정보 공공 API 서비스가 폐기되었습니다.");
        } else if ("20".equals(resultCode)) {
            sendErrorNotice("버스도착정보 공공 API 서비스가 접근 거부 상태입니다.");
        } else if ("22".equals(resultCode)) {
            sendErrorNotice("버스도착정보 공공 API 서비스의 요청 제한 횟수가 초과되었습니다.");
        } else if ("30".equals(resultCode)) {
            sendErrorNotice("등록되지 않은 버스도착정보 공공 API 서비스 키입니다.");
        } else if ("31".equals(resultCode)) {
            sendErrorNotice("버스도착정보 공공 API 서비스 키의 활용 기간이 만료되었습니다.");
        }
        return !"00".equals(resultCode);
    }

    private void sendErrorNotice(String message) {
        String className = this.getClass().getName();
        String cacheKey = String.format(CACHE_KEY_BUS_ERROR_ALERT, className);
        try {
            String cachedValue = stringRedisUtilStr.getDataAsString(cacheKey);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            if (cachedValue == null || Period.between(ZonedDateTime.parse(cachedValue).toLocalDate(), now.toLocalDate()).getDays() >= 1) {
                stringRedisUtilObj.setDataAsString(CACHE_KEY_BUS_ERROR_ALERT, now.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
                sender.noticeError(NotiSlack.builder()
                        .color("danger")
                        .title(className)
                        .text(message)
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
