package koreatech.in.service;

import koreatech.in.domain.Bus.BusRemainTime;
import koreatech.in.domain.Bus.SchoolBusCourse;

import java.util.List;


public interface BusService {
    BusRemainTime getRemainTime(String busType, String depart, String arrival) throws Exception;

    List<SchoolBusCourse> getCourses();

    String getTimetable(String busType, String region);
}
