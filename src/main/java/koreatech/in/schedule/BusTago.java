package koreatech.in.schedule;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import koreatech.in.domain.Bus.BusArrivalInfo;
import koreatech.in.domain.NotiSlack;
import koreatech.in.service.JsonConstructor;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.StringRedisUtilObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("busTagoSchedule")
public class BusTago {
    public static final List<List<String>> nodeIds = new ArrayList<List<String>>() {{
        add(new ArrayList<String>() {{
            add("CAB285000405");
            add("koreatech");
        }});
        add(new ArrayList<String>() {{
            add("CAB285000655");
            add("station-koreatech");
        }});
        add(new ArrayList<String>() {{
            add("CAB285000656");
            add("station-terminal");
        }});
        add(new ArrayList<String>() {{
            add("CAB285000686");
            add("terminal");
        }});
    }};

    public final int[] availableBus = {400, 401, 402, 493};

    private final String CITY_CODE = "34010";

    @Value("${OPEN_API_KEY}")
    private static String OPEN_API_KEY;

    @Autowired
    private StringRedisUtilObj stringRedisUtilObj;

    @Autowired
    private JsonConstructor con;

    @Autowired
    private SlackNotiSender sender;

    private static final String CACHE_KEY_BUS_ARRIVAL_INFO = "Tago@busArrivalInfo.%s.%s";

    private String requestBusArrivalInfo(String cityCode, List<String> nodeId) throws IOException {
        String urlBuilder = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList" + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + OPEN_API_KEY + // API Key
                "&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + URLEncoder.encode(cityCode, "UTF-8") + // 도시 코드
                "&" + URLEncoder.encode("nodeId", "UTF-8") + "=" + URLEncoder.encode(nodeId.get(0), "UTF-8") + // 정거장 ID
                "&_type=json";

        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        return sb.toString();
    }

    private List<Map<String, Object>> extractBusArrivalInfo(String response) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            JsonObject nodeInfo = new JsonParser().parse(response).getAsJsonObject();
            String resultCode = nodeInfo.getAsJsonObject("response").getAsJsonObject("header").getAsJsonObject("resultCode").getAsString();
            if (checkError(resultCode)) {
                return result;
            }

            int count = nodeInfo.getAsJsonObject("response").getAsJsonObject("body").get("totalCount").getAsInt();
            if (count <= 1) {
                BusArrivalInfo col = new BusArrivalInfo();
                if (count == 1) {
                    result.add(con.parseJsonObject(nodeInfo.getAsJsonObject("response").getAsJsonObject("body").getAsJsonObject("items").getAsJsonObject("item")));
                }
                return result;
            }
            result = con.parseJsonArrayWithObject(nodeInfo.getAsJsonObject("response").getAsJsonObject("body").getAsJsonObject("items").getAsJsonArray("item"));
        } catch (JsonSyntaxException e) {
            return result;
        }

        return result;
    }

    public void updateAndCacheBusArrivalInfo(String cityCode, List<String> nodeId) throws IOException {
        String cacheKey = getBusArrivalInfoCacheKey(cityCode, nodeId.get(0));
        List<Map<String, Object>> info = extractBusArrivalInfo(requestBusArrivalInfo(cityCode, nodeId));
        stringRedisUtilObj.setDataAsString(cacheKey, info);
    }

    @Scheduled(cron = "0 */3 * * * *")
    public void handle() {
        try {
            for (List<String> nodeId : nodeIds) {
//                System.out.println("updating...(" + CITY_CODE + "," + nodeId.get(0) + "," + nodeId.get(1) + ")\n");
                updateAndCacheBusArrivalInfo(CITY_CODE, nodeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBusArrivalInfoCacheKey(String cityCode, String nodeId) {
        return String.format(CACHE_KEY_BUS_ARRIVAL_INFO, cityCode, nodeId);
    }

    private List<Map<String, Object>> getBusArrivalInfo(String cityCode, String nodeId) {
        String cacheKey = getBusArrivalInfoCacheKey(cityCode, nodeId);
        List<Map<String, Object>> ret = new ArrayList<>();
        try {
            ret = (List<Map<String, Object>>) stringRedisUtilObj.getDataAsString(cacheKey, ret.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public List<Map<String, Object>> getBusArrivalInfo(String nodeId) {
        return getBusArrivalInfo(CITY_CODE, nodeId);
    }

    private boolean checkError(String resultCode) {
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
        return "00".equals(resultCode);
    }

    private void sendErrorNotice(String message) {
        sender.noticeError(NotiSlack.builder()
                .color("danger")
                .title(String.format("%s.%s", "BusTago", "requestBusArrivalInfo"))
                .text(message)
                .build());
    }
}
