package koreatech.in.service;

import koreatech.in.domain.Bus.BusRemainTime;
import koreatech.in.domain.Bus.SchoolBusCourse;

import java.util.ArrayList;


public interface BusService {
    BusRemainTime getRemainTime(String busType, String depart, String arrival) throws Exception;

    ArrayList<SchoolBusCourse> getCourses();

    String getTimetable(String busType, String region);
}
