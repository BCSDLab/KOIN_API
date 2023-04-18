package koreatech.in.domain.Bus;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import koreatech.in.domain.Version.BatchVersion;
import koreatech.in.domain.Version.VersionTypeEnum;
import koreatech.in.repository.VersionMapper;
import koreatech.in.util.StringRedisUtilObj;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Bus {

    static final Gson gson = new Gson();

    @Autowired
    StringRedisUtilObj stringRedisUtilObj;

    @Autowired
    StringRedisUtilStr stringRedisUtilStr;

    @Autowired
    VersionMapper versionMapper;

    String requestOpenAPI(String urlBuilder) {

        final int waitTime = 1000 * 2;
        try {
            URL url = new URL(urlBuilder);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(waitTime);
            conn.setReadTimeout(waitTime);
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
        } catch (IOException e) {
            return null;
        }
    }

    public abstract BusRemainTime getNowAndNextBusRemainTime(String busType, String depart, String arrival);

    public abstract List<? extends BusTimetable> getTimetables(String busType, String direction, String region);

    public abstract void cacheBusArrivalInfo();

    public abstract VersionTypeEnum getVersionType();

    protected void updateVersion() {
        VersionTypeEnum typeEnum = Optional.ofNullable(getVersionType())
                .orElseThrow(UnsupportedOperationException::new);

        versionMapper.upsertBusVersion(BatchVersion.from(typeEnum));
    }

    public abstract SingleBusTime searchBusTime(String busType, String depart, String arrival, LocalDateTime at);
}
