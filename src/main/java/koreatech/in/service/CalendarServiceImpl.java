package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.kut.Calendar;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.CalendarMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService{
    @Inject
    CalendarMapper calendarMapper;

    @Override
    public List<Calendar> getCalendars(String year) {
        if(year == null) {
            throw new PreconditionFailedException(new ErrorMessage("year is required", 0));
        }
        return calendarMapper.getCalendarsByYear(year);
    }
}
