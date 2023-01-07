package koreatech.in.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Callvan.Company;
import koreatech.in.service.CallvanService;
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
@Api(tags = "(Admin) Callvan", description = "콜밴")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.CALLVAN)
@Controller
public class AdminCallvanController {
    @Inject
    private CallvanService callvanService;

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/callvan/companies", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createCompany(@ApiParam(value = "(required: name, phone), (optional: pay_card, pay_bank, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Company company, BindingResult bindingResult) {
        return new ResponseEntity<Company>(callvanService.createCompanyForAdmin(company), HttpStatus.CREATED);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/callvan/companies/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateCompany(@ApiParam(value = "(optional: name, phone, pay_card, pay_bank, is_deleted)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Company company, BindingResult bindingResult, @ApiParam(required = true) @PathVariable("id") int id) {
        return new ResponseEntity<Company>(callvanService.updateCompanyForAdmin(company, id), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/callvan/companies/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteCompany(@ApiParam(required = true) @PathVariable("id") int id) {
        return new ResponseEntity<Map<String, Object>>(callvanService.deleteCompanyForAdmin(id), HttpStatus.OK);
    }
}
