package koreatech.in.dto.normal.mail;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
public class Mail {

    private static final String CERTIFICATION_CODE = "certification-code";
    private static final String EMAIL_ADDRESS = "email-address";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY_OF_MONTH = "day-of-month";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";


    private final String certificationCode;

    private final String emailAddress;

    private final LocalDateTime time;

    private final Map<String, Object> model = new HashMap<>();

    public Map<String, Object> convertToMapWithTimes(Mail mail) {
        model.put(CERTIFICATION_CODE, certificationCode);
        model.put(EMAIL_ADDRESS, emailAddress);
        model.put(YEAR, time.getYear());
        model.put(MONTH, time.getMonthValue());
        model.put(DAY_OF_MONTH, time.getDayOfMonth());
        model.put(HOUR, time.getHour());
        model.put(MINUTE, time.getMinute());
        return model;
    }

    public Map<String, Object> convertToMap(Mail mail) {
        model.put(CERTIFICATION_CODE, certificationCode);
        return model;
    }
}
