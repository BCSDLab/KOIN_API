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

    KOREATECH_TO_TERMINAL("koreatech", "terminal", "CAB285000405"),
    KOREATECH_TO_STATION("koreatech", "station", "CAB285000405"),

    TERMINAL_TO_KOREATECH("terminal", "koreatech", "CAB285000686"),
    TERMINAL_TO_STATION("terminal", "station", "CAB285000686"),

    STATION_TO_TERMINAL("station", "terminal", "CAB285000656"),
    STATION_TO_KOREATECH("station", "koreatech", "CAB285000655"),
    ;

    private final String depart;

    private final String arrival;

    private final String cityBusNodeID;

    public static final List<String> nodeIDs = new ArrayList<>();

    static {
        nodeIDs.addAll(Arrays.stream(values())
                .map(BusNodeEnum::getCityBusNodeID)
                .collect(Collectors.toSet()));
    }

    public static BusNodeEnum valueOf(String depart, String arrival) {
        for (BusNodeEnum busRouteEnum : BusNodeEnum.values()) {
            if (busRouteEnum.depart.equalsIgnoreCase(depart) && busRouteEnum.arrival.equalsIgnoreCase(arrival)) {
                return busRouteEnum;
            }
        }
        return null;
    }
}
