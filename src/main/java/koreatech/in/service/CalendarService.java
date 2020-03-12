package koreatech.in.service;


import koreatech.in.domain.kut.Calendar;

import java.util.List;
import java.util.Map;

public interface CalendarService {
    List<Calendar> getCalendars(String year);

    Map<String, Object> getTerm() throws Exception;

    String createTermForAdmin(String term);
}
