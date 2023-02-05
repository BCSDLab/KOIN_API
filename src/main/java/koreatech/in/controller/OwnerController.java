package koreatech.in.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.Auth.Authority;
import koreatech.in.annotation.Auth.Role;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.service.OwnerService;
import koreatech.in.util.StringXssChecker;
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

    @ApiResponses({
            @ApiResponse(code = 422, message = "- 이메일 주소가 올바르지 않을 경우 (code: 101008) \n\n" +
                    "- 이메일 도메인이 사용할 수 없는 경우 (code: 101009) \n\n",
                    response = ExceptionResponse.class),
    })
    @ApiOperation(value = "인증번호 전송 요청")
    @AuthExcept
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

    @ApiResponses({
            @ApiResponse(code = 409, message = "- 이미 인증이 완료된 이메일일 경우 (code: 121000) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(code = 410, message = "- 저장기간(`2시간`)이 만료된 이메일일 경우 (code: 101010) \n\n"+
                    "- 코드 인증 기한(`5분`)이 경과된 경우 (code: 121001) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(code = 422, message =
                    "- 이메일 도메인이 사용할 수 없는 경우 (code: 101009) \n\n" +
                    "- 인증 코드가 일치하지 않을 경우 (code: 121002) \n\n" ,
                    response = ExceptionResponse.class)
    })
    @ApiOperation(value = "인증번호 입력")
    @AuthExcept
    @RequestMapping(value = "/owners/verification/code", method = RequestMethod.POST)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> verifyCode(@RequestBody @Valid VerifyCodeRequest request,
                                             BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, new VerifyCodeRequest());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerService.certificate(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 409, message = "- 이미 인증이 완료된 이메일일 경우 (code: 121000) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(code = 410, message = "- 저장기간(`2시간`)이 만료된 이메일일 경우 (code: 101010) \n\n"+
                    "- 코드 인증 기한(`5분`)이 경과된 경우 (code: 121001) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(code = 422, message =
                    "- 이메일 도메인이 사용할 수 없는 경우 (code: 101009) \n\n" +
                            "- 인증 코드가 일치하지 않을 경우 (code: 121002) \n\n" ,
                    response = ExceptionResponse.class)
    })
    @ApiOperation(value = "인증번호 입력")
    @AuthExcept
    @RequestMapping(value = "/owners/verification/code", method = RequestMethod.POST)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> register(@RequestBody @Valid OwnerRegisterRequest request,
                                           BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerService.register(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
