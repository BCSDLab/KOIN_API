package koreatech.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import koreatech.in.dto.normal.bus.BusTimetableResponse;
import koreatech.in.service.BusService;

@Api(tags = "(Normal) Bus V2", description = "버스 V2")
@Controller
@RequestMapping(value = "v2/bus")
public class V2BusController {

    @Autowired
    private BusService busService;

    @RequestMapping(value = "/timetable", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<BusTimetableResponse> getTimetable(@ApiParam(value = "버스 종류(shuttle, commuting, express)", required = true) @RequestParam(value = "bus_type") String busType,
        @ApiParam(value = "등/하교(to, from)", required = true) @RequestParam(value = "direction") String direction,
        @ApiParam(value = "버스 노선 지역") @RequestParam(value = "region", required = false) String region) {

        return new ResponseEntity<>(busService.getTimetableWithUpdatedAt(busType, direction, region), HttpStatus.OK);
    }
}
