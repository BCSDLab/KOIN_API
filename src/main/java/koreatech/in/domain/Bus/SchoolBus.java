package koreatech.in.domain.Bus;

import org.springframework.stereotype.Component;

@Component("schoolBus")
public class SchoolBus implements Bus {
    private static final String SCHOOL_BUS_TIMETABLE_CACHE_KEY = "Tago@busTimetable.%s.%s";

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String target, String depart, String arrival) {
        return null;
    }
}
