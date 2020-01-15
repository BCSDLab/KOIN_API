package koreatech.in.service;


import koreatech.in.domain.kut.Calendar;

import java.util.List;

public interface CalendarService {
    public List<Calendar> getCalendars(String year);
}
