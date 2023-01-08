package koreatech.in.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Faq.Faq;
import koreatech.in.service.FaqService;
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
@Api(tags = "(Admin) Faq", description = "질문")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.COMMUNITY)
@Controller
public class AdminFaqController {
    @Inject
    private FaqService faqService;

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/faqs", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createFaq(@ApiParam(value = "(required: question, answer), (optional: circle_id, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Faq faq, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Faq>(faqService.createFaqForAdmin(faq), HttpStatus.CREATED);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/faqs/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateFaq(@ApiParam(value = "(required: question, answer), (optional: circle_id, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Faq faq, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {
        return new ResponseEntity<Faq>(faqService.updateFaqForAdmin(faq, id), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/faqs/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteFaq(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(faqService.deleteFaqForAdmin(id), HttpStatus.OK);
    }
}
