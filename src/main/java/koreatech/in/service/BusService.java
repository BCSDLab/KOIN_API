package koreatech.in.service;

import koreatech.in.domain.Bus.BusTimetable;
import koreatech.in.domain.Bus.BusRemainTime;
import koreatech.in.domain.Bus.SchoolBusCourse;
import koreatech.in.domain.Bus.SingleBusTime;
import koreatech.in.dto.normal.bus.BusTimetableResponse;

import java.util.List;


public interface BusService {
    BusRemainTime getRemainTime(String busType, String depart, String arrival) throws Exception;

    List<SchoolBusCourse> getCourses();

    List<? extends BusTimetable> getTimetable(String busType, String direction, String region);

    List<SingleBusTime> searchTimetable(String date, String time, String depart, String arrival);

    BusTimetableResponse getTimetableWithUpdatedAt(String busType, String direction, String region);
}
