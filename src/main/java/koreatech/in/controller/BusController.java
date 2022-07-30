package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Bus.Course;
import koreatech.in.service.BusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Map;

@Controller
public class BusController {
    private static final Logger logger = LoggerFactory.getLogger(BusController.class);

    @Autowired
    private BusService busService;

    // 시내버스
    @RequestMapping(value = "/buses", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getBus(@ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "depart") String depart,
                                               @ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "arrival") String arrival) throws Exception {
        return new ResponseEntity<>(busService.getBus(depart, arrival), HttpStatus.OK);
    }

    // 통학버스 노선
    @RequestMapping(value = "/bus/courses", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<Course>> getCourses() {
        return new ResponseEntity<>(busService.getCourses(), HttpStatus.OK);
    }

    // 통학버스 시간표
    @RequestMapping(value = "/bus/schedule", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseEntity<String> getSchedule(@ApiParam(value = "버스 종류(shuttle, commuting, express)", required = true) @RequestParam(value = "bus_type") String busType,
                                       @ApiParam(value = "버스 노선 지역") @RequestParam(value = "region", required = false) String region) {
        return new ResponseEntity<>(busService.getSchedule(busType, region), HttpStatus.OK);
    }
}
