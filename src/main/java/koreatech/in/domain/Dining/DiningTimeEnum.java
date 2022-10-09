package koreatech.in.domain.Dining;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DiningTimeEnum {
    BREAKFAST("아침"),
    LUNCH("점심"),
    DINNER("저녁"),
    ;

    private final String diningTimeKor;

    public static DiningTimeEnum findByKorName(String korName) throws IllegalArgumentException {
        return Arrays.stream(DiningTimeEnum.values())
                .filter(diningTimeEnum -> diningTimeEnum.getDiningTimeKor().equalsIgnoreCase(korName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static String convertToEngName(String korName) throws IllegalArgumentException {
        return findByKorName(korName).name();
    }
}
