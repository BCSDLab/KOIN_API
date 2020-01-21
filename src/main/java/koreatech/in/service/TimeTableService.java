package koreatech.in.service;

import koreatech.in.domain.TimeTable.Lecture;
import koreatech.in.domain.TimeTable.TimeTable;

import java.util.ArrayList;
import java.util.Map;

public interface TimeTableService {
    ArrayList<Lecture> getLectureList(String semester_date) throws Exception;

    ArrayList<Map<String,Object>> getSemesterList() throws Exception;

    Map<String, Object> getTimeTables(String semester) throws Exception;

    Map<String, Object> createTimeTables(String timetable_log) throws Exception;

    Map<String, Object> updateTimeTable(String timetable_log) throws Exception;

    Map<String, Object> deleteTimeTableAll(String semester) throws Exception;

    Map<String, Object> deleteTimeTableById(int id) throws Exception;

}
