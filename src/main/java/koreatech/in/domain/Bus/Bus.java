package koreatech.in.domain.Bus;

import com.google.gson.Gson;
import koreatech.in.util.StringRedisUtilObj;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class Bus {

    static final Gson gson = new Gson();

    @Value("${OPEN_API_KEY}")
    String OPEN_API_KEY;

    @Autowired
    StringRedisUtilObj stringRedisUtilObj;

    @Autowired
    StringRedisUtilStr stringRedisUtilStr;

    String requestOpenAPI(String urlBuilder) throws IOException {
        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(1000 * 5);
        conn.setReadTimeout(1000 * 5);
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

    public abstract BusRemainTime getNowAndNextBusRemainTime(String depart, String arrival);

    public abstract void cacheBusArrivalInfo();
}
