package koreatech.in.domain.Bus;

import com.google.gson.Gson;
import koreatech.in.util.StringRedisUtilObj;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public abstract class Bus {

    static final Gson gson = new Gson();

    @Autowired
    StringRedisUtilObj stringRedisUtilObj;

    @Autowired
    StringRedisUtilStr stringRedisUtilStr;

    String requestOpenAPI(String urlBuilder) throws IOException {
        final int waitTime = 1000 * 3;

        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(waitTime);
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300 ? conn.getInputStream() : conn.getErrorStream(),
                StandardCharsets.UTF_8
        ));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();
        conn.disconnect();

        return response.toString();
    }

    public abstract BusRemainTime getNowAndNextBusRemainTime(String busType, String depart, String arrival);

    public abstract List<? extends BusTimetable> getTimetables(String busType, String direction, String region);

    public abstract void cacheBusArrivalInfo();

    public abstract SingleBusTime searchBusTime(String busType, String depart, String arrival, LocalDate date, LocalTime time);
}
