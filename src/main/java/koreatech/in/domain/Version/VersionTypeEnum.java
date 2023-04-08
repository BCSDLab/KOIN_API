package koreatech.in.domain.Version;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.NotFoundException;

public enum VersionTypeEnum {
    ANDROID("android"),
    TIMETABLE("timetable"),
    SHUTTLE("shuttle_bus_timetable"),
    CITY("city_bus_timetable"),
    EXPRESS("express_bus_timetable"),
    ;

    private final String type;

    VersionTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static void validates(String typeName) {
        Set<String> types = Arrays.stream(VersionTypeEnum.values())
                .map(VersionTypeEnum::getType)
                .collect(Collectors.toSet());

        if(types.contains(typeName)) {
            return;
        }

        throw new NotFoundException(new ErrorMessage("유지중인 타입이 아닙니다. 올바른 타입을 입력해주세요.", 0));
    }
}
