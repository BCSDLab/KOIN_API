package koreatech.in.service;

import com.google.gson.*;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.TimeTable.Lecture;
import koreatech.in.domain.TimeTable.Semester;
import koreatech.in.domain.TimeTable.TimeTable;
import koreatech.in.domain.User.User;
import koreatech.in.exception.*;
import koreatech.in.repository.TimeTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("timeTableService")
public class TimeTableServiceImpl implements TimeTableService {

    @Resource(name = "timeTableMapper")
    private TimeTableMapper timeTableMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Override
    public ArrayList<Lecture> getLectureList(String semester_date) throws Exception {
        if (timeTableMapper.checkSemesterDateExists(semester_date) == 0) {
            throw new NotFoundException(new ErrorMessage("Data not found.", 0));
        }

        return timeTableMapper.getLectureList(semester_date);
    }

    @Override
    public ArrayList<Semester> getSemesterList() throws Exception {
        return timeTableMapper.getSemesterList();
    }

    @Override
    public Map<String, Object> getTimeTables(String semester) throws Exception {
        User user = jwtValidator.validate();

        // DB koin.semester에 대응하는 semester 존재 여부 확인
        if (timeTableMapper.checkSemesterExists(semester) == 0) {
            throw new PreconditionFailedException(new ErrorMessage("There's no such semester.", 0));
        }

        ArrayList<TimeTable> timeTableList = timeTableMapper.getTimeTableList(user.getId(), timeTableMapper.getSemesterId(semester));

        ArrayList<Map<String, Object>> timetables = new ArrayList<Map<String, Object>>();

        for(TimeTable timeTable : timeTableList) {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("id", timeTable.getId());
            map.put("code", timeTable.getCode());
            map.put("class_title", timeTable.getClass_title());
            map.put("class_time", timeTable.getClass_timeAsArray());
            map.put("class_place", timeTable.getClass_place());
            map.put("professor", timeTable.getProfessor());
            map.put("grades", timeTable.getGrades());
            map.put("lecture_class", timeTable.getLecture_class());
            map.put("target", timeTable.getTarget());
            map.put("regular_number", timeTable.getRegular_number());
            map.put("design_score", timeTable.getDesign_score());
            map.put("department", timeTable.getDepartment());
            map.put("memo", timeTable.getMemo());

            timetables.add(map);
        }

        int currentGrades = timeTableList.stream()
            .mapToInt(timeTable -> Integer.parseInt(timeTable.getGrades()))
            .sum();

        Map<String, Object> retMap = new HashMap<String, Object>() {{
            put("timetable", timetables);
            put("semester", semester);
            put("grades", currentGrades);
            put("total_grades", calculateTotalGrades(user.getId()));
        }};

        return retMap;
    }

    @Transactional
    @Override
    public Map<String, Object> createTimeTables(String timetable_log) throws Exception {
        User user = jwtValidator.validate();

        // 요청값 JsonObject인지 판별
        JsonParser jsonParser = new JsonParser();
        try {
            if (!jsonParser.parse(timetable_log).isJsonObject()) {
                throw new PreconditionFailedException(new ErrorMessage("Not a valid JsonObject.", 0));
            }
        } catch (Exception e) {
            throw new PreconditionFailedException(new ErrorMessage("Not a valid JsonObject.", 0));
        }

        // 요청값에 timetable, semester key 존재 여부 확인
        JsonObject jsonObject = jsonParser.parse(timetable_log).getAsJsonObject();
        if (!jsonObject.has("timetable") || !jsonObject.has("semester")) {
            throw new PreconditionFailedException(new ErrorMessage("Required value unsatisfied.", 0));
        }

        String semester = jsonObject.get("semester").getAsString();
        Integer semester_id = timeTableMapper.getSemesterId(semester);

        if (semester_id == null) {
            throw new PreconditionFailedException(new ErrorMessage("There's no such semester.", 0));
        }
        if (!jsonObject.get("timetable").isJsonArray()) {
            throw new PreconditionFailedException(new ErrorMessage("Not a valid JsonArray.", 0));
        }

        JsonArray jsonArray = jsonObject.getAsJsonArray("timetable");
        Gson gson = new Gson();
        for(JsonElement jsonElement: jsonArray) {
            jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has("class_title") || jsonObject.get("class_title").getAsString().equals("")
                || !jsonObject.has("class_time")
                || !jsonObject.has("grades")) {
                //                    || new Gson().fromJson(jsonObject.get("class_time"), Integer[].class).length < 1
                throw new PreconditionFailedException(new ErrorMessage("Required value unsatisfied.", 0));
            }

            TimeTable timeTable = gson.fromJson(jsonElement, TimeTable.class);
            timeTable.setUser_id(user.getId());
            timeTable.setSemester_id(semester_id);

            timeTableMapper.createTimeTable(timeTable);
        }

        return getTimeTables(semester);
    }

    @Transactional
    @Override
    public Map<String, Object> updateTimeTable(String timetable_log) throws Exception {
        User user = jwtValidator.validate();

        JsonParser jsonParser = new JsonParser();
        try {
            if (!jsonParser.parse(timetable_log).isJsonObject()) {
                throw new PreconditionFailedException(new ErrorMessage("Not a valid JsonObject.", 0));
            }
        } catch (Exception e) {
            throw new PreconditionFailedException(new ErrorMessage("Not a valid JsonObject.", 0));
        }

        JsonObject jsonObject = jsonParser.parse(timetable_log).getAsJsonObject();
        if (!jsonObject.has("timetable") || !jsonObject.has("semester")) {
            throw new PreconditionFailedException(new ErrorMessage("Required value unsatisfied.", 0));
        }
        else if (!jsonObject.get("timetable").isJsonArray()) {
            throw new PreconditionFailedException(new ErrorMessage("Not a valid JsonArray.", 0));
        }


        String semester = jsonObject.get("semester").getAsString();
        Integer semester_id = timeTableMapper.getSemesterId(semester);

        if (semester_id == null) {
            throw new PreconditionFailedException(new ErrorMessage("There's no such semester.", 0));
        }

        JsonArray jsonArray = jsonObject.getAsJsonArray("timetable");
        Gson gson = new Gson();
        for(JsonElement jsonElement: jsonArray) {
            jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has("id") || !jsonObject.has("class_title") || jsonObject.get("class_title").getAsString().equals("")
                || !jsonObject.has("class_time")
                //                    || jsonObject.getAsJsonArray("class_time").size() < 1
                || !jsonObject.has("grades")) {
                throw new PreconditionFailedException(new ErrorMessage("Required value unsatisfied.", 0));
            }

            TimeTable timeTable_old = timeTableMapper.getTimeTable(jsonObject.get("id").getAsInt());

            if (timeTable_old == null || timeTable_old.getIs_deleted()) {
                throw new PreconditionFailedException(new ErrorMessage("There's no such timetable.", 0));
            }

            if (!timeTable_old.hasGrantDelete(user)) {
                throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));
            }

            TimeTable timeTable = gson.fromJson(jsonElement, TimeTable.class);
            timeTable_old.update(timeTable);
            timeTable_old.setSemester_id(semester_id);

            timeTableMapper.updateTimeTable(timeTable_old);
        }

        return getTimeTables(semester);
    }

    @Transactional
    @Override
    public Map<String, Object> deleteTimeTableAll(String semester) throws Exception {
        User user = jwtValidator.validate();

        if (timeTableMapper.checkSemesterExists(semester) == 0) {
            throw new PreconditionFailedException(new ErrorMessage("There's no such semester.", 0));
        }

        timeTableMapper.deleteTimeTableAll(user.getId(), semester);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public Map<String, Object> deleteTimeTableById(int id) throws Exception {
        User user = jwtValidator.validate();

        TimeTable timeTable = timeTableMapper.getTimeTable(id);
        if (timeTable == null || timeTable.getIs_deleted())
            throw new NotFoundException(new ErrorMessage("There is no such timetable", 0));

        if (!timeTable.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        timeTable.setIs_deleted(true);
        timeTableMapper.updateTimeTable(timeTable);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    private int calculateTotalGrades(int userId) {
        int totalGrades = 0;
        List<Semester> semesters = timeTableMapper.getSemesterList();

        for(Semester semester : semesters){
            totalGrades += timeTableMapper.getTimeTableList(userId, semester.getId()).stream()
                .mapToInt(timeTable -> Integer.parseInt(timeTable.getGrades()))
                .sum();
        }

        return totalGrades;
    }
}

