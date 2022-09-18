package koreatech.in.domain.Bus;

import org.springframework.stereotype.Component;

@Component("schoolBus")
public class SchoolBus extends Bus {

    private static final String SCHOOL_BUS_TIMETABLE_CACHE_KEY = "Tago@busTimetable.%s.%s";

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String depart, String arrival) {
        return null;
    }

    @Override
    public void cacheBusArrivalInfo() {

    }
}
