package koreatech.in.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.service.CalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.io.IOException;

@ApiOff @ApiIgnore @Deprecated
@Api(tags = "(Admin) Calendar", description = "학기 일정")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.COMMUNITY)
@Controller
public class AdminCalendarController {
    @Inject
    private CalendarService calendarService;

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/term", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> createTerm(@ApiParam(required = true, value = "10: 1학기 정규, 11: 1학기 계절, 12: 1학기 계절 이후, 20: 2학기 정규, 21: 2학기 계절, 22: 2학기 계절 이후")
                                      @RequestBody String term) throws IOException {
        return new ResponseEntity<>(calendarService.createTermForAdmin(term), HttpStatus.CREATED);
    }
}
