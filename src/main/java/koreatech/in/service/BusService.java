package koreatech.in.service;

import koreatech.in.domain.Bus.Course;

import java.util.ArrayList;
import java.util.Map;


public interface BusService {
    public Map<String, Object> getBus(String depart, String arrival) throws Exception;

    public ArrayList<Course> getCourses();

    public String getSchedule(String busType, String region);
}
