package koreatech.in.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import javax.validation.Valid;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.Auth.Authority;
import koreatech.in.annotation.Auth.Role;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.Login;
import koreatech.in.annotation.ParamValid;
import koreatech.in.domain.User.User;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.RequestDataInvalidResponse;
import koreatech.in.dto.normal.user.owner.request.OwnerChangePasswordRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.dto.normal.user.owner.response.OwnerResponse;
import koreatech.in.dto.normal.user.owner.response.VerifyCodeResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.service.OwnerService;
import koreatech.in.util.StringXssChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
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
            @ApiResponse(
                    code = 404,
                    message ="- 존재하지 않는 이메일일 경우 (code: 101022)",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)"
                            + "- 이메일 주소가 올바르지 않을 경우 (code: 101008) \n\n"
                            + "- 이메일 도메인이 사용할 수 없는 경우 (code: 101009) \n\n",
                    response = ExceptionResponse.class

            )
    })
    @ApiOperation(value = "비밀번호 변경 인증번호 전송 요청")
    @AuthExcept
    @RequestMapping(value = "/owners/password/reset/verification", method = RequestMethod.POST)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> verifyEmailToChangePassword(@RequestBody @Valid VerifyEmailRequest request,
                                              BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerService.requestVerificationToChangePassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(
                    code = 409,
                    message = "- 이미 인증이 완료된 이메일일 경우 (code: 101011) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 410,
                    message = "- 저장기간(`2시간`)이 만료된 이메일일 경우 (code: 101010) \n\n"
                            + "- 코드 인증 기한(`5분`)이 경과된 경우 (code: 121001) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)"
                            + "- 인증 코드가 일치하지 않을 경우 (code: 121002) \n\n",
                    response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "비밀번호 변경 인증번호 입력")
    @AuthExcept
    @RequestMapping(value = "/owners/password/reset/send", method = RequestMethod.POST)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> verifyCodeToChangePassword(@RequestBody @Valid VerifyCodeRequest request,
                                                  BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerService.certificateToChangePassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(
                    code = 409,
                    message = "- 이미 인증이 완료된 이메일일 경우 (code: 101011) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 410,
                    message = "- 저장기간(`2시간`)이 만료된 이메일일 경우 (code: 101010) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)"
                            + "- 이메일 주소가 올바르지 않을 경우 (code: 101008) \n\n"
                            + "- 이메일 도메인이 사용할 수 없는 경우 (code: 101009) \n\n",
                    response = ExceptionResponse.class

            )
    })
    @ApiOperation(value = "비밀번호 변경")
    @AuthExcept
    @RequestMapping(value = "/owners/password/reset", method = RequestMethod.PUT)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> inputPasswordToChangePassword(@RequestBody @Valid OwnerChangePasswordRequest request,
                                                                BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerService.inputPasswordToChangePassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(
                    code = 409,
                    message = "- 이미 인증이 완료된 이메일일 경우 (code: 101011) \n\n"
                            + "- 이미 누군가 사용중인 이메일일 경우 (code: 101013)",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)"
                            + "- 이메일 주소가 올바르지 않을 경우 (code: 101008) \n\n"
                            + "- 이메일 도메인이 사용할 수 없는 경우 (code: 101009) \n\n",
                    response = ExceptionResponse.class

            )
    })
    @ApiOperation(value = "회원가입 인증번호 전송 요청")
    @AuthExcept
    @RequestMapping(value = "/owners/verification/email", method = RequestMethod.POST)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> verifyEmail(@RequestBody @Valid VerifyEmailRequest request,
                                              BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerService.requestVerification(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(
                    code = 409,
                    message = "- 이미 인증이 완료된 이메일일 경우 (code: 101011) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 410,
                    message = "- 저장기간(`2시간`)이 만료된 이메일일 경우 (code: 101010) \n\n"
                            + "- 코드 인증 기한(`5분`)이 경과된 경우 (code: 121001) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)"
                            + "- 인증 코드가 일치하지 않을 경우 (code: 121002) \n\n",
                    response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "인증번호 입력")
    @AuthExcept
    @RequestMapping(value = "/owners/verification/code", method = RequestMethod.POST)
    @ParamValid
    public @ResponseBody
    ResponseEntity<VerifyCodeResponse> verifyCode(@RequestBody @Valid VerifyCodeRequest request,
                                             BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        VerifyCodeResponse verifyCodeResponse = ownerService.certificate(request);
        return new ResponseEntity<>(verifyCodeResponse, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(
                    code = 409,
                    message = "- 인증이 되지 않은 이메일일 경우 (code: 101012)"
                            + "\n\n- 이미 누군가 사용중인 이메일일 경우 (code: 101013)"
                            + "\n\n- 이미 누군가 사용중인 사업자등록번호일 경우 (code: 101021)",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 410,
                    message = "- 저장기간(`2시간`)이 만료된 이메일일 경우 (code: 101010) \n\n",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000) \n\n"
                            + "- 사업자등록번호, 첨부파일 둘 중 하나만 있을 경우 (error code: 100000)",
                    response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "회원가입 요청")
    @AuthExcept
    @RequestMapping(value = "/owners/register", method = RequestMethod.POST)
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


    @ApiResponses({
            @ApiResponse(code = 401, message
                    = "토큰에 대한 회원 정보가 없을 때 (code: 101000)"
                    , response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)",
                    response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "사장님 정보 조회", notes = "- 사장님 권한[+가게 권한 부여] 필요",
            authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/owner", method = RequestMethod.GET)
    @ParamValid
    public @ResponseBody
    ResponseEntity<OwnerResponse> getOwner(@Login User loggedInUser) {
        OwnerResponse owner = ownerService.getOwner(loggedInUser);
        return new ResponseEntity<>(owner, HttpStatus.OK);
    }


    @ApiResponses({
            @ApiResponse(code = 401
                    , message = "- 토큰에 대한 회원 정보가 없을 때 (code: 101000)"
                    , response = ExceptionResponse.class),
            @ApiResponse(code = 403
                    , message = "- 권한이 없을 때 (code: 100003)"
                    , response = ExceptionResponse.class),
            @ApiResponse(code = 404
                    , message = "- 요청한 첨부파일이 존재하지 않을 때(code: 121003)"
                    , response = ExceptionResponse.class),
            @ApiResponse(code = 422
                    , message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)"
                    , response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "사장님 첨부파일 삭제", notes = "- 사장님 권한[+가게 권한 부여] 필요",
            authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/owners/attachment/{id}", method = RequestMethod.DELETE)
    @ParamValid
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteAttachment(
            @ApiParam(required = true) @PathVariable("id") Integer attachmentId) {
        ownerService.deleteAttachment(attachmentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiResponses({
            @ApiResponse(code = 401
                    , message = "- 토큰에 대한 회원 정보가 없을 때 (code: 101000)"
                    , response = ExceptionResponse.class),
            @ApiResponse(code = 403
                    , message = "- 권한이 없을 때 (code: 100003)"
                    , response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)",
                    response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "사장님 정보 수정", notes = "- 사장님 권한[+가게 권한 부여] 필요", authorizations = {
            @Authorization(value = "Authorization")})
    @RequestMapping(value = "/owner", method = RequestMethod.PUT)
    @ParamValid
    public @ResponseBody
    ResponseEntity<OwnerResponse> update(@RequestBody @Valid OwnerUpdateRequest request, BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }
        OwnerResponse ownerResponse = ownerService.update(request);

        return new ResponseEntity<>(ownerResponse, HttpStatus.OK);
    }
}
