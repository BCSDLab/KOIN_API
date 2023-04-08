package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import javax.inject.Inject;
import koreatech.in.domain.Version.Version;
import koreatech.in.service.VersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VersionController {
    @Inject
    private VersionService versionService;

    @RequestMapping(value = "/versions/{type}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Version> getVersion(
            @PathVariable(value = "type")
            @ApiParam(value = "타입 이름 \n\n"
                    + "- `android` (안드로이드)\n"
                    + "- `shuttle_bus_timetable` (셔틀, 통학 버스)\n"
                    + "- `express_bus_timetable` (대성 고속)\n"
                    + "- `city_bus_timetable` (시내 버스)\n"
                    , example = "city_bus_timetable", required = true)
            String type) throws Exception {

        return new ResponseEntity<>(versionService.getVersion(type), HttpStatus.OK);
    }
}
