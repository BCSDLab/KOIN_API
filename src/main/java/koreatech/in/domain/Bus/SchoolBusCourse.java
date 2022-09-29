package koreatech.in.domain.Bus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SchoolBusCourse {

    private final String bus_type;

    private final String direction;

    private final String region;

    public SchoolBusCourse(String bus_type, String direction, String region) {
        this.bus_type = bus_type;
        this.direction = direction;
        this.region = region;
    }
}
