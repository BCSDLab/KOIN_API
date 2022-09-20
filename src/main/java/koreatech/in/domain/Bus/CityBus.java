package koreatech.in.domain.Bus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.istack.internal.NotNull;
import koreatech.in.domain.NotiSlack;
import koreatech.in.util.SlackNotiSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CityBus extends Bus {

    private static final String CHEONAN_CITY_CODE = "34010";

    private static final int[] AVAILABLE_CITY_BUS = {400, 401, 402, 492, 493};

    private static final String CACHE_KEY_BUS_ARRIVAL_INFO = "Tago@busArrivalInfo:cityBus:%s";

    private static final String CACHE_KEY_BUS_ERROR_ALERT = "Tago@busErrorAlert:%s";

    private static final Type arrivalInfoType = new TypeToken<List<CityBusArrivalInfo>>() {
    }.getType();

    @Autowired
    private SlackNotiSender sender;

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String depart, String arrival) {
        BusRemainTime response = new BusRemainTime();
        try {
            BusNodeEnum busNodeEnum = Objects.requireNonNull(BusNodeEnum.valueOf(depart, arrival));
            List<CityBusArrivalInfo> arrivalInfos = Objects.requireNonNull(getArrivalTimes(busNodeEnum));

            if (arrivalInfos.size() == 1) {
                return new BusRemainTime.Builder()
                        .nowRemainTime(new BusRemainTime.RemainTime(arrivalInfos.get(0).getRouteno(), arrivalInfos.get(0).getArrtime()))
                        .build();
            } else if (arrivalInfos.size() > 1) {
                return new BusRemainTime.Builder()
                        .nowRemainTime(new BusRemainTime.RemainTime(arrivalInfos.get(0).getRouteno(), arrivalInfos.get(0).getArrtime()))
                        .nextRemainTime(new BusRemainTime.RemainTime(arrivalInfos.get(1).getRouteno(), arrivalInfos.get(1).getArrtime()))
                        .build();
            }
        } catch (JsonSyntaxException | NullPointerException e) {
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private List<CityBusArrivalInfo> getArrivalTimes(@NotNull BusNodeEnum busNodeEnum) {
        return Optional.ofNullable(getArrivalTimesFromCache(busNodeEnum))
                .orElseGet(() -> getArrivalTimesFromReal(busNodeEnum));
    }

    private List<CityBusArrivalInfo> getArrivalTimesFromCache(@NotNull BusNodeEnum busNodeEnum) {
        try {
            String cacheKey = getCacheKey(busNodeEnum.getCityBusNodeID());
            String cachedValue = stringRedisUtilStr.getDataAsString(cacheKey);
            return gson.fromJson(cachedValue, arrivalInfoType);
        } catch (NullPointerException | IOException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<CityBusArrivalInfo> getArrivalTimesFromReal(@NotNull BusNodeEnum busNodeEnum) {
        return getArrivalTimesFromReal(busNodeEnum.getCityBusNodeID());
    }

    private List<CityBusArrivalInfo> getArrivalTimesFromReal(@NotNull String nodeID) {
        try {
            String arrivalTimes = getBusResult(CHEONAN_CITY_CODE, nodeID);
            List<CityBusArrivalInfo> arrivalInfos = extractBusArrivalInfo(arrivalTimes)
                    .stream()
                    .filter(info -> IntStream.of(AVAILABLE_CITY_BUS).anyMatch(busNumber -> busNumber == info.getRouteno()))
                    .sorted()
                    .collect(Collectors.toList());
            cacheBusArrivalInfo(nodeID, arrivalInfos);

            return arrivalInfos;
        } catch (Exception e) {
            return null;
        }
    }

    private String getBusResult(String cityCode, String nodeId) throws IOException {
        String urlBuilder = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList" + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + OPEN_API_KEY + // API Key
                "&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + URLEncoder.encode(cityCode, "UTF-8") + // 도시 코드
                "&" + URLEncoder.encode("nodeId", "UTF-8") + "=" + URLEncoder.encode(nodeId, "UTF-8") + // 정거장 ID
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "30" +
                "&_type=json";
        return requestOpenAPI(urlBuilder);
    }

    private List<CityBusArrivalInfo> extractBusArrivalInfo(String response) {
        List<CityBusArrivalInfo> result = new ArrayList<>();
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
                result.add(gson.fromJson(item, CityBusArrivalInfo.class));
            }
            return result;
        } catch (JsonSyntaxException e) {
            return result;
        }
    }

    public void cacheBusArrivalInfo() {
        for (String nodeID : BusNodeEnum.nodeIDs) {
            getArrivalTimesFromReal(nodeID);
        }
    }

    private void cacheBusArrivalInfo(String nodeId, List<CityBusArrivalInfo> cityBusArrivalInfos) throws IOException {
        String cacheKey = getCacheKey(nodeId);
        stringRedisUtilObj.setDataAsString(cacheKey, cityBusArrivalInfos);
    }

    private String getCacheKey(String nodeId) {
        return String.format(CACHE_KEY_BUS_ARRIVAL_INFO, nodeId);
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
