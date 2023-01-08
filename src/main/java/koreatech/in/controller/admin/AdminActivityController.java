package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Homepage.Activity;
import koreatech.in.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Map;

@ApiIgnore
@Api(tags = "(Admin) Activity", description = "BCSDLab 활동")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.BCSDLAB)
@Controller
public class AdminActivityController {
    @Inject
    private ActivityService activityService;

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/activities", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getActivities(@ApiParam(value = "(optional:year)", required = false) @RequestParam(value = "year", required = false) String year) throws Exception {
        return new ResponseEntity<Map<String, Object>>(activityService.getActivitiesForAdmin(year), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/activities/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getActivity(@ApiParam(value = "(required:id)", required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Activity>(activityService.getActivityForAdmin(id), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/activities", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createActivity(@ApiParam(value = "(required: title, date)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Activity activity, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Activity>(activityService.createActivityForAdmin(activity), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/activities/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateActivity(@ApiParam(value = "", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Activity activity, BindingResult bindingResult, @ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Activity>(activityService.updateActivityForAdmin(activity, id), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/activities/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteActivity(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(activityService.deleteActivityForAdmin(id), HttpStatus.OK);
    }
}
