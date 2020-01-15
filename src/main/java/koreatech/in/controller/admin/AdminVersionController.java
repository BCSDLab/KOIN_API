package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Version.Version;
import koreatech.in.service.VersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.VERSION)
@Controller
public class AdminVersionController {
    @Inject
    private VersionService versionService;

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/versions", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getVersions() throws Exception {

        return new ResponseEntity<List<Version>>(versionService.getVersionsForAdmin(), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/versions", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createVersion(@ApiParam(value = "(required: type, version)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Version version, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<Version>(versionService.createVersionForAdmin(version), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/versions/{type}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateVersion(@ApiParam(value = "(required: version)", required = true) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Version version, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "type") String type) throws Exception {

        return new ResponseEntity<Version>(versionService.updateVersionForAdmin(version, type), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/versions/{type}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteVersion(@ApiParam(required = true) @PathVariable(value = "type") String type) throws Exception {

        return new ResponseEntity<Map<String, Object>>(versionService.deleteVersionForAdmin(type), HttpStatus.OK);
    }
}
