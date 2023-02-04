package koreatech.in.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static Date addMinute(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, amount);

        return calendar.getTime();
    }

    public static Boolean isExpired(Date origin, Date candidate) {
        return origin.compareTo(candidate) < 0;
    }
}
