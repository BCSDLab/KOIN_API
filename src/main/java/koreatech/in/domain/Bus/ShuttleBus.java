package koreatech.in.domain.Bus;

import koreatech.in.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShuttleBus extends Bus {

    private static final String SCHOOL_BUS_TIMETABLE_CACHE_KEY = "Tago@busTimetable.%s.%s";

    @Autowired
    private BusRepository busRepository;

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String depart, String arrival) {
        return null;
    }

    @Override
    public void cacheBusArrivalInfo() {
    }
}
