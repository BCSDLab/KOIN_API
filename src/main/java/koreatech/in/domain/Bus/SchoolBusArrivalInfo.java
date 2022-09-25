package koreatech.in.domain.Bus;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "bus_timetables")
@Getter
@ToString
public class SchoolBusArrivalInfo {
    @Id
    private String id;

    private String bus_type;

    private String direction;

    private String region;

    private List<Map<String, Object>> courses;
}
