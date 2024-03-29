package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Bus.BusRemainTime;
import koreatech.in.domain.Bus.BusTimetable;
import koreatech.in.domain.Bus.SchoolBusCourse;
import koreatech.in.domain.Bus.SingleBusTime;
import koreatech.in.dto.normal.bus.BusTimetableResponse;
import koreatech.in.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api(tags = "(Normal) Bus", description = "버스")
@Controller
@RequestMapping(value = "/bus")
public class BusController {

    @Autowired
    private BusService busService;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<BusRemainTime> getRemainTime(@ApiParam(value = "버스 종류(city, express, shuttle, commuting)", required = true) @RequestParam(value = "bus_type") String busType,
                                                @ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "depart") String depart,
                                                @ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "arrival") String arrival) throws Exception {

        return new ResponseEntity<>(busService.getRemainTime(busType, depart, arrival), HttpStatus.OK);
    }

    @RequestMapping(value = "/courses", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<SchoolBusCourse>> getCourses() {

        return new ResponseEntity<>(busService.getCourses(), HttpStatus.OK);
    }

    @RequestMapping(value = "/timetable", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<? extends BusTimetable>> getTimetable(@ApiParam(value = "버스 종류(shuttle, commuting, express)", required = true) @RequestParam(value = "bus_type") String busType,
                                                              @ApiParam(value = "등/하교(to, from)", required = true) @RequestParam(value = "direction") String direction,
                                                              @ApiParam(value = "버스 노선 지역") @RequestParam(value = "region", required = false) String region) {

        return new ResponseEntity<>(busService.getTimetable(busType, direction, region), HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<SingleBusTime>> getTimetable(@ApiParam(value = "yyyy-MM-dd", required = true, example = "2022-10-06") @RequestParam(value = "date") String date,
                                                     @ApiParam(value = "HH:mm", required = true, example = "14:10") @RequestParam(value = "time") String time,
                                                     @ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "depart") String depart,
                                                     @ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "arrival") String arrival) {

        return new ResponseEntity<>(busService.searchTimetable(date, time, depart, arrival), HttpStatus.OK);
    }

    @RequestMapping(value = "/timetable/v2", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<BusTimetableResponse> getTimetableV2(@ApiParam(value = "버스 종류(shuttle, commuting, express)", required = true) @RequestParam(value = "bus_type") String busType,
        @ApiParam(value = "등/하교(to, from)", required = true) @RequestParam(value = "direction") String direction,
        @ApiParam(value = "버스 노선 지역") @RequestParam(value = "region", required = false) String region) {

        return new ResponseEntity<>(busService.getTimetableWithUpdatedAt(busType, direction, region), HttpStatus.OK);
    }
}
