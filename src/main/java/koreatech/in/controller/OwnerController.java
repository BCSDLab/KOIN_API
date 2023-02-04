package koreatech.in.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import javax.validation.Valid;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.Auth.Authority;
import koreatech.in.annotation.Auth.Role;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.normal.owner.request.VerifyEmailRequest;
import koreatech.in.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "(Normal) Owner", description = "사장님")
@Auth(role = Role.OWNER, authority = Authority.SHOP)
@Controller
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @AuthExcept
    @ApiOperation(value = "인증번호 전송 요청", authorizations = {@Authorization("Authorization")})
    @RequestMapping(value = "/owners/verficitation/email", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> verifyEmail(@RequestBody @Valid VerifyEmailRequest request, BindingResult bindingResult) {
        //TODO 23.02.04. request body안에 있는 이메일, XSS 검증 필요한지
        ownerService.requestVerification(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
