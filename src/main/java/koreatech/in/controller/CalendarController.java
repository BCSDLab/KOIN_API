package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import koreatech.in.annotation.ApiOff;
import koreatech.in.domain.kut.Calendar;
import koreatech.in.service.CalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
public class CalendarController {
    @Inject
    private CalendarService calendarService;

    @ApiOff
    @RequestMapping(value = "/calendars", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<Calendar>> getCalendars(@ApiParam(required = true) @RequestParam(value = "year") String year) {
        return new ResponseEntity<>(calendarService.getCalendars(year), HttpStatus.OK);
    }

    @RequestMapping(value = "/term", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getTerm() throws Exception {
        return new ResponseEntity<>(calendarService.getTerm(), HttpStatus.OK);
    }
}
