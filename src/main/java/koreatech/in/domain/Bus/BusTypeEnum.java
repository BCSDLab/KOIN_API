package koreatech.in.domain.Bus;

import koreatech.in.util.BeanUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BusTypeEnum {

    CITY(CityBus.class, "시내버스"),
    EXPRESS(IntercityBus.class, "대성고속"),
    SHUTTLE(ShuttleBus.class, "셔틀버스"),
    COMMUTING(CommutingBus.class, "통학버스"),
    ;

    private final Class<? extends Bus> busClass;

    private final String busName;

    private static final Map<BusTypeEnum, Bus> busTypeEnumMap = new EnumMap<BusTypeEnum, Bus>(BusTypeEnum.class) {{

        Arrays.stream(BusTypeEnum.values())
                .forEach(busTypeEnum -> {
                    put(busTypeEnum, BeanUtil.getBean(busTypeEnum.getBusClass()));
                });
    }};

    private static BusTypeEnum findBy(String busType) throws IllegalArgumentException {

        return Arrays.stream(BusTypeEnum.values())
                .filter(busTypeEnum -> busTypeEnum.name().equalsIgnoreCase(busType))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static Bus createBus(String busType) {

        return busTypeEnumMap.get(findBy(busType));
    }

    public Bus getBus() {

        return busTypeEnumMap.get(this);
    }
}
