package koreatech.in.repository;

import koreatech.in.domain.kut.Calendar;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarMapper {
    @Select("SELECT * FROM koin.calendar_universities WHERE year = #{year}")
    public List<Calendar> getCalendarsByYear(String year);
}
