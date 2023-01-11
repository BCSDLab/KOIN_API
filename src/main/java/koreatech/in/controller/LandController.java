package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.*;
import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.service.LandService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Map;

@Api(tags = "(Normal) Land", description = "복덕방")
@Auth(role = Auth.Role.USER)
@Controller
public class LandController {
    @Inject
    private LandService landService;


    @AuthExcept
    @RequestMapping(value = "/lands", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLands() throws Exception {

        return new ResponseEntity<Map<String, Object>>(landService.getLands(), HttpStatus.OK);
    }

//    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})

    @AuthExcept
    @RequestMapping(value = "/lands/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getland(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(landService.getLand(id), HttpStatus.OK);
    }

    @ApiIgnore
    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lands/evaluate/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateEvaluate(@ApiParam(value = "(required: content, score)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) LandComment landComment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<LandComment>(landService.updateLandComment(landComment, id), HttpStatus.CREATED);
    }

    @ApiIgnore
    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lands/evaluate/{id}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity evaluateLand(@ApiParam(value = "(required: content, score)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) LandComment landComment, BindingResult bindingResult, @ApiParam(required = true)@PathVariable(value = "id") int id) throws Exception {
        LandComment clear = new LandComment();
        return new ResponseEntity<LandComment>(landService.createLandComment((LandComment)StringXssChecker.xssCheck(landComment, clear), id), HttpStatus.CREATED);
    }

    @ApiIgnore
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lands/evaluate/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteEvaluate(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(landService.deleteLandComment(id), HttpStatus.OK);
    }

}
