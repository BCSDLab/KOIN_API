package koreatech.in.domain.Bus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public enum MajorStationEnum {

    KOREATECH("koreatech", "한기대",
            new ArrayList<String>() {{
                add("학교");
                add("코리아텍");
            }}),
    STATION("station", "천안역",
            new ArrayList<String>() {
            }),
    TERMINAL("terminal", "터미널",
            new ArrayList<String>() {{
                add("야우리");
            }}),
    ;

    private final String engName;

    private final String korName;

    private final List<String> synonyms;

    public static MajorStationEnum findByKorName(String korName) throws IllegalArgumentException {
        return Arrays.stream(MajorStationEnum.values())
                .filter(majorStationEnum -> majorStationEnum.korName.equalsIgnoreCase(korName) || majorStationEnum.synonyms.contains(korName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static MajorStationEnum findByEngName(String engName) throws IllegalArgumentException {
        return Arrays.stream(MajorStationEnum.values())
                .filter(majorStationEnum -> majorStationEnum.engName.equalsIgnoreCase(engName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
