package koreatech.in.service;


import koreatech.in.domain.kut.Calendar;

import java.util.List;

public interface CalendarService {
    List<Calendar> getCalendars(String year);

    String getTerm() throws Exception;

    String createTermForAdmin(String term);
}
