package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import koreatech.in.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "(Normal) Activity", description = "BCSDLab 활동")
@Controller
public class ActivityController {
    @Resource(name = "activityService")
    private ActivityService activityService;

    @RequestMapping(value = "/activities", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getActivities(@ApiParam(value = "optional", required = false) @RequestParam(value = "year", required = false) String year) throws Exception {
        return new ResponseEntity<Map<String, Object>>(activityService.getActivities(year), HttpStatus.OK);
    }
}
