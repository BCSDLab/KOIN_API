package koreatech.in.dto;

import koreatech.in.domain.User.EmailAddress;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Mail {

    private static final String CERTIFICATION_CODE = "certification-code";
    private static final String EMAIL_ADDRESS = "email-address";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY_OF_MONTH = "day-of-month";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";


    private String certificationCode;

    private EmailAddress emailAddress;

    private LocalDateTime time;

    private Map<String, Object> model;

    private Mail() {
        model = new HashMap<>();
    }

    public static Mail builder() {
        return new Mail();
    }

    public Mail certificationCode(String certificationCode) {
        model.put(CERTIFICATION_CODE, certificationCode);
        return this;
    }

    public Mail emailAddress(String emailAddress) {
        model.put(EMAIL_ADDRESS, emailAddress);
        return this;
    }

    public Mail dateTime(LocalDateTime dateTime) {
        model.put(YEAR, dateTime.getYear());
        model.put(MONTH, dateTime.getMonthValue());
        model.put(DAY_OF_MONTH, dateTime.getDayOfMonth());
        model.put(HOUR, dateTime.getHour());
        model.put(MINUTE, dateTime.getMinute());
        return this;
    }

    public Map<String, Object> build() {
        return model;
    }
}
