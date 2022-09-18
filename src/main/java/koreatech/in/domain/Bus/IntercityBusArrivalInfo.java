package koreatech.in.domain.Bus;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class IntercityBusArrivalInfo implements Comparable<IntercityBusArrivalInfo> {

    private String arrPlaceNm;

    private String arrPlandTime;

    private String depPlaceNm;

    private int charge;

    private String depPlandTime;

    private String gradeNm;

    private String routeId;

    @Override
    public int compareTo(IntercityBusArrivalInfo o) {
        return this.depPlandTime.compareTo(o.depPlandTime);
    }
}
