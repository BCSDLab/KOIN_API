package koreatech.in.service;

import koreatech.in.domain.Bus.SchoolBusCourse;

import java.util.ArrayList;
import java.util.Map;


public interface BusService {
    public Map<String, Object> getBus(String depart, String arrival) throws Exception;

    public ArrayList<SchoolBusCourse> getCourses();

    public String getTimetable(String busType, String region);
}
