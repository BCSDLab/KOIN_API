package koreatech.in.domain.Bus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RegionEnum {

    천안("cheonan"),
    청주("cheongju"),
    대전("daejeon"),
    서울("seoul"),
    세종("sejong"),
    ;

    private final String region;

    public static RegionEnum findByEngName(String region) {
        return Arrays.stream(RegionEnum.values())
                .filter(regionEnum -> regionEnum.region.equalsIgnoreCase(region))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}