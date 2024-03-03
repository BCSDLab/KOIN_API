package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.inject.Inject;
import koreatech.in.domain.Version.Version;
import koreatech.in.domain.Version.VersionTypeEnum;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.service.VersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "(Normal) Version", description = "버전")
@Controller
public class VersionController {
    @Inject
    private VersionService versionService;

    @ApiOperation(value = "버전 조회", notes = "- 권한 필요 없음")
    @ApiResponses({
            @ApiResponse(code = 404, message
                    = "- 존재하지 않는 버전 타입일 때 (code: 122000) \n\n"
                    + "- 버전 타입에 해당하는 버전이 없을 때 (code: 122001) \n\n"
                    , response = ExceptionResponse.class),
    })
    @RequestMapping(value = "/versions/{type}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Version> getVersion(
            @PathVariable(value = "type")
            @ApiParam(value = "타입 이름 \n\n"
                    + "- `android`(안드로이드)\n"
                    + "- `timetable`(시간표)\n"
                    + "- `shuttle_bus_timetable`(셔틀, 통학 버스)\n"
                    + "- `express_bus_timetable`(대성 고속)\n"
                    + "- `city_bus_timetable`(시내 버스)\n"
                    , example = "city_bus_timetable", required = true)
            String type) throws Exception {

        VersionTypeEnum.validates(type);

        return new ResponseEntity<>(versionService.getVersion(type), HttpStatus.OK);
    }
}
