package koreatech.in.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import javax.validation.Valid;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.Auth.Authority;
import koreatech.in.annotation.Auth.Role;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
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
    @ApiResponses({
            @ApiResponse(code = 422, message = "- 이메일 주소가 올바르지 않을 경우 (code: 101008) \n\n" +
                    "- 이메일 도메인이 사용할 수 없는 경우 (code: 101009) \n\n",
                    response = ExceptionResponse.class),
    })
    @ApiOperation(value = "인증번호 전송 요청", authorizations = {@Authorization("Authorization")})
    @RequestMapping(value = "/owners/verification/email", method = RequestMethod.POST)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> verifyEmail(@RequestBody @Valid VerifyEmailRequest request,
                                              BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, new VerifyEmailRequest());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerService.requestVerification(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
