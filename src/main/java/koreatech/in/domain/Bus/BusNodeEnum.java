package koreatech.in.domain.Bus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BusNodeEnum {

    KOREATECH_TO_TERMINAL(MajorStationEnum.KOREATECH, MajorStationEnum.TERMINAL, "from", "CAB285000405"),
    KOREATECH_TO_STATION(MajorStationEnum.KOREATECH, MajorStationEnum.STATION, "from", "CAB285000405"),

    TERMINAL_TO_KOREATECH(MajorStationEnum.TERMINAL, MajorStationEnum.KOREATECH, "to", "CAB285000686"),
    TERMINAL_TO_STATION(MajorStationEnum.TERMINAL, MajorStationEnum.STATION, "to", "CAB285000686"),

    STATION_TO_TERMINAL(MajorStationEnum.STATION, MajorStationEnum.TERMINAL, "from", "CAB285000656"),
    STATION_TO_KOREATECH(MajorStationEnum.STATION, MajorStationEnum.KOREATECH, "to", "CAB285000655"),
    ;

    private final MajorStationEnum depart;

    private final MajorStationEnum arrival;

    private final String direction;

    private final String cityBusNodeID;

    public static final List<String> nodeIDs = new ArrayList<>();

    static {
        nodeIDs.addAll(Arrays.stream(values())
                .map(BusNodeEnum::getCityBusNodeID)
                .collect(Collectors.toSet()));
    }

    public static BusNodeEnum valueOf(String depart, String arrival) throws IllegalArgumentException {
        for (BusNodeEnum busRouteEnum : BusNodeEnum.values()) {
            if (busRouteEnum.depart.getEngName().equalsIgnoreCase(depart) && busRouteEnum.arrival.getEngName().equalsIgnoreCase(arrival)) {
                return busRouteEnum;
            }
        }
        throw new IllegalArgumentException();
    }

    public static BusNodeEnum valueOf(MajorStationEnum depart, MajorStationEnum arrival) throws IllegalArgumentException {
        if (depart == null || arrival == null) {
            throw new NullPointerException();
        }

        for (BusNodeEnum busRouteEnum : BusNodeEnum.values()) {
            if (busRouteEnum.depart.getEngName().equalsIgnoreCase(depart.getEngName()) && busRouteEnum.arrival.getEngName().equalsIgnoreCase(arrival.getEngName())) {
                return busRouteEnum;
            }
        }
        throw new IllegalArgumentException();
    }
}
