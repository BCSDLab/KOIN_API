package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.RequestDataInvalidResponse;
import koreatech.in.dto.normal.auth.TokenRefreshResponse;
import koreatech.in.dto.normal.auth.TokenRefreshRequest;
import koreatech.in.dto.normal.user.request.AuthTokenRequest;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.request.FindPasswordRequest;
import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.normal.user.response.AuthResponse;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.student.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.student.response.StudentResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.service.UserService;
import koreatech.in.util.StringXssChecker;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "(Normal) User", description = "회원")
@Auth(role = Auth.Role.USER)
@Controller
public class UserController {
    public static final String MAIL_SUCCESS_REGISTER_CONFIG = "mail/success_register_config";
    public static final String MAIL_ERROR_CONFIG = "mail/error_config";
    public static final String MODEL_KEY_ERROR_MESSAGE = "errorMessage";
    @Inject
    private UserService userService;

    @ApiOperation("로그인")
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n\n" +
                                               "- 아이디에 대한 회원 정보가 없을 때 (code: 101000) \n\n" +
                                               "- 비밀번호가 일치하지 않을 때 (code: 101001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 로그인 권한이 없을 때 (code: 100003) \n" +
                                               "  - 신원이 학생일 경우: 아직 이메일 인증이 되지 않았기 때문 \n" +
                                               "  - 신원이 사장님일 경우: 아직 어드민페이지에서 권한 부여를 하지 않았기 때문", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request, BindingResult bindingResult) throws Exception {
        LoginResponse response = userService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "로그아웃", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001) \n\n" +
                                               "토큰의 유효시간이 만료되었을 때 (code: 100004) \n\n" +
                                               "토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public ResponseEntity<EmptyResponse> logout() {
        userService.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "액세스 토큰 재발급", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001) \n\n" +
                    "토큰의 유효시간이 만료되었을 때 (code: 100004) \n\n" +
                    "토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/user/refresh", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<TokenRefreshResponse> refresh(@ApiParam(required = true) @RequestBody TokenRefreshRequest request) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        TokenRefreshResponse tokenRefreshResponse = userService.refresh(request);
        return new ResponseEntity<>(tokenRefreshResponse, HttpStatus.CREATED);
    }

    @AuthExcept
    @ParamValid
    @ApiResponses({
            @ApiResponse(
                    code = 409,
                    message = "- 이미 존재하는 닉네임일 경우 (code: 101002) \n\n"
                            + "- 이미 사용중인 이메일일 경우 (code: 101013)",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)\n\n"
                            + "  - 한국기술교육대학교 포탈의 이메일 형식('koreatech.ac.kr')이 아닌 경우 (code: 101014)\n\n"
                            + "  - 유효하지 않는 이메일 주소인 경우 (code: 101008)\n\n"
                            + "  - 유효하지 않는 이메일 도메인인 경우 (code: 101009)\n\n"
                            + "  - 학생의 학번 형식이 아닌 경우 (code: 101015)\n\n"
                            + "  - 학생의 전공 형식이 아닌 경우 (code: 101016)\n\n",
                    response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "학생 회원가입", notes= "- 권한 필요 없음")
    @RequestMapping(value = "/user/student/register", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> studentRegister(
            @ApiParam(required = true) @RequestBody @Validated StudentRegisterRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest) {

        // TODO: velocity template 에게 인증 url host를 넣기 위해 url 데이터를 register에 넘겼는데, 이 방법 대신 하단 링크를 참고하여 plugin을 붙이는 방법으로 해결하기.
        // https://developer.atlassian.com/server/confluence/confluence-objects-accessible-from-velocity/

        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        userService.StudentRegister(request, getHost(httpServletRequest));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Auth(role = Auth.Role.STUDENT)
    @ApiOperation(value = "학생 정보 조회", notes= "- 학생 권한만 필요", authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message
                    = "토큰에 대한 회원 정보가 없을 때 (code: 101000)"
                    , response = ExceptionResponse.class),
    })
    @RequestMapping(value = "/user/student/me", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StudentResponse> getStudent() {
        StudentResponse response = userService.getStudent();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Auth(role = Auth.Role.STUDENT)@ParamValid
    @ApiResponses({
            @ApiResponse(
                    code = 401,
                    message = "- JWT 토큰이 유효하지 않은 경우 (code: 100001)",
                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 409,
                    message = "- 이미 존재하는 닉네임일 경우 (code: 101002)",

                    response = ExceptionResponse.class),
            @ApiResponse(
                    code = 422,
                    message = "- 요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)\n\n"
                            + "  - 학생의 학번 형식이 아닌 경우 (code: 101015)\n\n"
                            + "  - 학생의 전공 형식이 아닌 경우 (code: 101016)",
                    response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "학생 업데이트", notes= "- 학생 권한만 필요", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/student/me", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<StudentResponse> updateUser(@RequestBody @Valid StudentUpdateRequest request, BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        StudentResponse studentResponse = userService.updateStudent(request);

        return new ResponseEntity<>(studentResponse, HttpStatus.CREATED);
    }

    @ParamValid
    @Auth(role = Auth.Role.OWNER)
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/owner/me", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateOwnerInformation(@ApiParam(value = "(optional: password, name, nickname, gender, identity, is_graduated, major, student_number, phone_number, email)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) Owner owner, BindingResult bindingResult) throws Exception {

        Owner clear = new Owner();

        return new ResponseEntity<>(userService.updateOwnerInformation((Owner) StringXssChecker.xssCheck(owner, clear)), HttpStatus.CREATED);
    }


    @ApiOperation(value = "탈퇴", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n\n" +
                                               "- 토큰의 유효시간이 만료되었을 때 (code: 100004) \n\n" +
                                               "- 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> withdraw() {
        userService.withdraw();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "닉네임 중복 체크", notes = "닉네임 중복시 http status 409, 중복이 아닐시 http status 200을 응답합니다.")
    @ApiResponses({
            @ApiResponse(code = 409, message = "- 닉네임이 중복될 때 (code: 101002)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- nickname의 제약 조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @AuthExcept
    @RequestMapping(value = "/user/check/nickname", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<EmptyResponse> checkDuplicationOfNickname(
            @ApiParam(value = "닉네임 \n " +
                              "- not null \n " +
                              "- 10자 이하 \n " +
                              "- 공백 문자로만 이루어져있으면 안됨", required = true) @RequestParam("nickname") String nickname) {
        checkNicknameConstraintViolation(nickname);

        userService.checkUserNickname(nickname);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: javax validation 으로 query parameter에 대한 제약조건 검사가 불가능하여 메소드로 만듬. 추후 검사 방법이 있을지 알아보기.
    private void checkNicknameConstraintViolation(String nickname) {
        if (nickname == null) {
            throw new BaseException(ExceptionInformation.NICKNAME_SHOULD_NOT_BE_NULL);
        }
        if (nickname.length() > 10) {
            throw new BaseException(ExceptionInformation.NICKNAME_MAXIMUM_LENGTH_IS_10);
        }
        if (StringUtils.isBlank(nickname)) {
            throw new BaseException(ExceptionInformation.NICKNAME_MUST_NOT_BE_BLANK);
        }
    }

    @ApiOperation(value = "비밀번호 초기화(변경) 메일 발송")
    @ApiResponses({
            @ApiResponse(code = 404, message = "이메일에 대한 회원이 조회되지 않을 때 (code: 101003)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/user/find/password", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> changePasswordConfig(@RequestBody @Valid FindPasswordRequest request, BindingResult bindingResult, HttpServletRequest servletRequest) {
    // TODO: 23.02.12. 박한수 현재: 익명사용자가 특정 회원의 비밀번호 변경 요청을 시도할 수 있음 -> 개선안: jwt 토큰에 있는 사용자의 이메일로 비밀변경 요청을 시도하게 해야 함.

        // TODO: velocity template 에 인증 url에 들어갈 host를 넣기 위해 reigster에 url 데이터를 넘겼는데 추후 이 방법 없애고 plugin을 붙이는 방법으로 해결해보기
        // https://developer.atlassian.com/server/confluence/confluence-objects-accessible-from-velocity/

        userService.changePasswordConfig(request, getHost(servletRequest));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiIgnore
    @AuthExcept
    @RequestMapping(value = "/user/authenticate", method = RequestMethod.GET)
    public ModelAndView authenticate(@RequestParam("auth_token") AuthTokenRequest request) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        AuthResponse authResponse = userService.authenticate(request);

        return makeModelAndViewFor(authResponse);
    }

    private static ModelAndView makeModelAndViewFor(AuthResponse authResponse) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(makeViewNameFor(authResponse));

        if(!authResponse.isSuccess()) {
            modelAndView.addObject(MODEL_KEY_ERROR_MESSAGE, authResponse.getErrorMessage());
        }

        return modelAndView;
    }

    private static String makeViewNameFor(AuthResponse authResponse) {
        if(!authResponse.isSuccess()) {
            return MAIL_ERROR_CONFIG;
        }
        return MAIL_SUCCESS_REGISTER_CONFIG;
    }

    @ApiIgnore
    @AuthExcept
    @RequestMapping(value = "/user/change/password/config", method = RequestMethod.GET)
    public String changePasswordInput(@RequestParam("reset_token") String resetToken, Model model) {
        boolean isAwaitingUserFindPassword = userService.changePasswordInput(resetToken);

        if (!isAwaitingUserFindPassword) {
            return MAIL_ERROR_CONFIG;
        }

        model.addAttribute("resetToken", resetToken);
        return "mail/change_password_config";
    }

    @ApiIgnore
    @AuthExcept
    @RequestMapping(value = "/user/change/password/submit", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> changePasswordAuthenticate(@RequestBody Map<String, Object> params, @RequestParam("reset_token") String resetToken) {
        String password = params.get("password").toString();
        boolean success = userService.changePasswordAuthenticate(password, resetToken);

        return new HashMap<String, Object>() {{
            put("success", success);
        }};
    }

    @ApiResponses({
            @ApiResponse(code = 409, message = "- 이미 누군가 사용중인 이메일일 경우 (code: 101013)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- email의 제약 조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ApiOperation(value = "이메일 중복 체크")
    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/user/check/email", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<EmptyResponse> checkUserEmailExist(
            @ApiParam(value = "이메일 \n " +
                    "- not null \n " +
                    "- 이메일 형식이어야 함 \n " +
                    "- 공백 문자로만 이루어져있으면 안됨", required = true) @RequestParam("address") @Valid CheckExistsEmailRequest request) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        userService.checkExists(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    private String getHost(HttpServletRequest request) {
        String schema = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder url = new StringBuilder();
        url.append(schema).append("://");
        url.append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        return url.toString();
    }
}
