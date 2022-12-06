package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Circle.Circle;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.service.CircleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.CIRCLE)
@Controller
public class AdminCircleController {
    @Inject
    private CircleService circleService;

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/circles", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCircles(@ModelAttribute("criteria") Criteria criteria) throws Exception {
        return new ResponseEntity<Map<String, Object>>(circleService.getCirclesForAdmin(criteria), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/circles", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createCircle(@ApiParam(value = "(required: name, category, line_description), (optional: logo_url, description, link_urls, background_img_url, professor, location, major_business, introduce_url, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Circle circle, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Circle>(circleService.createCircleForAdmin(circle), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/circles/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCircle(@ApiParam(required = true) @PathVariable int id) throws Exception {
        return new ResponseEntity<Circle>(circleService.getCircleForAdmin(id), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/circles/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<Circle> updateCircle(@ApiParam(value = "(optional: name, category, line_description, logo_url, description, link_urls, background_img_url, professor, location, major_business, introduce_url, is_deleted)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Circle circle, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {
        return new ResponseEntity<Circle>(circleService.updateCircleForAdmin(circle, id), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/circles/{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity deleteCircle(@ApiParam(required = true) @PathVariable int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(circleService.deleteCircleForAdmin(id), HttpStatus.OK);
    }
}
