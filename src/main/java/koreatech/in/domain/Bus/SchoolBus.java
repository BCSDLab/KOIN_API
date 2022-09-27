package koreatech.in.domain.Bus;

import koreatech.in.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SchoolBus extends Bus {

    @Autowired
    protected BusRepository busRepository;

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String depart, String arrival) {
        return null;
    }

    @Override
    public List<? extends BusTimetable> getTimetables(String busType, String direction, String region) {

        SchoolBusArrivalInfo arrivalInfo = busRepository.findByCourse(busType, direction, region);
        return Optional.ofNullable(arrivalInfo)
                .map(SchoolBusArrivalInfo::getRoutes)
                .orElseGet(ArrayList::new);
    }

    @Override
    public void cacheBusArrivalInfo() {
        throw new UnsupportedOperationException();
    }
}
