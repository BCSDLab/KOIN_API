package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.domain.TimeTable.Lecture;
import koreatech.in.domain.TimeTable.Semester;
import koreatech.in.service.TimeTableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Map;

@Api(tags = "(Normal) TimeTable", description = "시간표")
@Auth(role = Auth.Role.STUDENT)
@Controller
public class TimeTableController {

    @Inject
    private TimeTableService timeTableService;

    @AuthExcept
    @RequestMapping(value = "/lectures", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLectureList(@ApiParam(value = "학기 (예시:20191)", required = false) @RequestParam(value = "semester_date", defaultValue = "") String semester_date) throws Exception {
        return new ResponseEntity<ArrayList<Lecture>>(timeTableService.getLectureList(semester_date), HttpStatus.OK);
    }

    @AuthExcept
    @RequestMapping(value = "/semesters", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getSemesterList() throws Exception {
        return new ResponseEntity<ArrayList<Semester>>(timeTableService.getSemesterList(), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/timetables", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getTimetabless(@ApiParam(value = "학기 (예시:20191)", required = true) @RequestParam(value = "semester") String semester) throws Exception {
        return new ResponseEntity<Map<String, Object>>(timeTableService.getTimeTables(semester), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/timetables", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createTimeTables(@ApiParam(value = "json value (예시: {\"timetable\":[...], \"semester\":\"20191\"}" +
                                                      "  Timetable REQUIRED class_time, class_title, grades", required = true) @RequestBody String timetable_log) throws Exception {
        return new ResponseEntity<Map<String, Object>>(timeTableService.createTimeTables(timetable_log), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/timetables", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateTimeTable(@ApiParam(value = "json value (예시: {\"timetable\":[...], \"semester\":\"20191\"}" +
                                                     "  Timetable REQUIRED id, class_time, class_title, grades", required = true) @RequestBody String timetable_log) throws Exception {
        return new ResponseEntity<Map<String, Object>>(timeTableService.updateTimeTable(timetable_log), HttpStatus.CREATED);
    }

    @ApiOff @ApiIgnore
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/timetables", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteTimeTableAll(@ApiParam(value = "학기 (예시:20191)", required = true) @RequestParam(value = "semester") String semester) throws Exception {
        return new ResponseEntity<Map<String, Object>>(timeTableService.deleteTimeTableAll(semester), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/timetable", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteTimeTableById(@ApiParam(value = "스케줄의 uid", required = true) @RequestParam(value = "id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(timeTableService.deleteTimeTableById(id), HttpStatus.OK);
    }
}