package koreatech.in.schedule;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import koreatech.in.domain.BusArrivalInfo;
import koreatech.in.service.JsonConstructor;
import koreatech.in.util.SlackNotiSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public static List<List<String>> nodeIds = new ArrayList<List<String>>() {{
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
    private static String OPEN_API_KEY;
    private static String cityCode = "34010";
    private static ValueOperations<String, List<Map<String, Object>>> valueOps;
    public int[] avaliableBus = {400, 401, 402, 493};

    @Autowired
    SlackNotiSender slackNotiSender;

    private static JsonConstructor con;

    private String CACHE_KEY_BUS_ARRIVAL_INFO = "Tago@busArrivalInfo.%s.%s";

    @Value("${OPEN_API_KEY}")
    private void setOpenApiKey(String key) {
        OPEN_API_KEY = key;
    }

    @Autowired
    private void setCon(JsonConstructor jsonConstructor) {
        con = jsonConstructor;
    }

    private static List<Map<String, Object>> requestBusArrivalInfo(String cityCode, List<String> nodeId) throws IOException {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + OPEN_API_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + URLEncoder.encode(cityCode, "UTF-8")); /*도시코드*/
        urlBuilder.append("&" + URLEncoder.encode("nodeId", "UTF-8") + "=" + URLEncoder.encode(nodeId.get(0), "UTF-8")); /*정류소ID*/
        urlBuilder.append("&_type=json");

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("built url: " + url);
//        System.out.println("Response code: " + conn.getResponseCode());
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

        if (!con.isJsonObject(sb.toString())) return result;

        JsonObject nodeInfo = new JsonParser().parse(sb.toString()).getAsJsonObject();

        if (!nodeInfo.has("response") || !nodeInfo.getAsJsonObject("response").has("body") || !nodeInfo.getAsJsonObject("response").getAsJsonObject("body").has("totalCount")) {
            return result;
        }

        int count = nodeInfo.getAsJsonObject("response").getAsJsonObject("body").get("totalCount").getAsInt();

        Gson gson = new Gson();
        if (count <= 1) {
            BusArrivalInfo col = new BusArrivalInfo();
            if (count == 1) {
                result.add(con.parseJsonObject(nodeInfo.getAsJsonObject("response").getAsJsonObject("body").getAsJsonObject("items").getAsJsonObject("item")));
            }
            return result;
        }

        result = con.parseJsonArrayWithObject(nodeInfo.getAsJsonObject("response").getAsJsonObject("body").getAsJsonObject("items").getAsJsonArray("item"));

        return result;
    }

    @Resource(name = "redisTemplate")
    public void setValueOps(ValueOperations<String, List<Map<String, Object>>> _valueOps) {
        valueOps = _valueOps;
    }

    private void updateAndCacheBusArrivalInfo(String cityCode, List<String> nodeId) throws IOException {
        String cacheKey = getBusArrivalInfoCacheKey(cityCode, nodeId.get(0));

        List<Map<String, Object>> info = requestBusArrivalInfo(cityCode, nodeId);

        valueOps.set(cacheKey, info);
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void handle() {
        try {
            for (List<String> nodeId : nodeIds) {
                System.out.println("updating...(" + cityCode + "," + nodeId.get(0) + "," + nodeId.get(1) + ")\n");
                updateAndCacheBusArrivalInfo(cityCode, nodeId);
            }
        } catch (NullPointerException e) {
//            sendError(e);
            System.out.println(e.toString());
        } catch (Exception e) {
//            sendError(e);
            System.out.println(e.toString() + "\n");
        }
    }

    private String getBusArrivalInfoCacheKey(String cityCode, String nodeId) {
        return String.format(CACHE_KEY_BUS_ARRIVAL_INFO, cityCode, nodeId);
    }

    private List<Map<String, Object>> getBusArrivalInfo(String cityCode, String nodeId) {
        String cacheKey = getBusArrivalInfoCacheKey(cityCode, nodeId);
        return valueOps.get(cacheKey);
    }

    public List<Map<String, Object>> getBusArrivalInfo(String nodeId) {
        return getBusArrivalInfo(cityCode, nodeId);
    }

//    private void sendError(Exception e) {
//        NotiSlack slack_message = new NotiSlack();
//
//        slack_message.setColor("danger");
//        slack_message.setTitle("DiningMenu command error");
//        slack_message.setText(e.toString());
//
//        slackNotiSender.noticeError(slack_message, "버스 정보 업데이트", "busTago");
//    }

}
