package koreatech.in.domain.Version;

public enum VersionTypeEnum {
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
}
