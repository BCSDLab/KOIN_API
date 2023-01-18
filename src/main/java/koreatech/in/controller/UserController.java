package koreatech.in.controller;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.response.StudentResponse;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import koreatech.in.service.UserService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Api(tags = "(Normal) User", description = "회원")
@Auth(role = Auth.Role.USER)
@Controller
public class UserController {
    @Inject
    private UserService userService;

    @ApiOperation("로그인")
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001) \n\n" +
                                               "아이디에 대한 회원 정보가 없을 때 (code: 101000) \n\n" +
                                               "비밀번호가 일치하지 않을 때 (code: 101001)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = ExceptionResponse.class)
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

    @AuthExcept
    @ParamValid
    @ApiOperation(value = "(required: account, password), (optional: name, nickname, gender, identity, is_graduated, major, student_number, phone_number)")
    @RequestMapping(value = "/user/student/register", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity studentRegister(
            @ApiParam(required = true) @RequestBody @Validated StudentRegisterRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest) throws Exception {

        // TODO: velocity template 에 인증 url에 들어갈 host를 넣기 위해 reigster에 url 데이터를 넘겼는데 추후 이 방법 없애고 plugin을 붙이는 방법으로 해결해보기
        // https://developer.atlassian.com/server/confluence/confluence-objects-accessible-from-velocity/

        StudentRegisterRequest clear = new StudentRegisterRequest();

        return new ResponseEntity<Map<String, Object>>(userService.StudentRegister(StringXssChecker.xssCheck(request, clear), getHost(httpServletRequest)), HttpStatus.CREATED);
    }

    @Auth(role = Auth.Role.STUDENT)
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/student/me", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getStudent() throws Exception {
        Student student = userService.getStudent();
        StudentResponse response = new StudentResponse(student);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @Auth(role = Auth.Role.STUDENT)
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/student/me", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateStudentInformation(@ApiParam(value = "(optional: password, name, nickname, gender, identity, is_graduated, major, student_number, phone_number)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) UpdateUserRequest request, BindingResult bindingResult) throws Exception {
        UpdateUserRequest clear = new UpdateUserRequest();
        return new ResponseEntity<>(userService.updateStudentInformation(StringXssChecker.xssCheck(request, clear)), HttpStatus.CREATED);
    }

    @ParamValid
    @Auth(role = Auth.Role.OWNER)
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/owner/me", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateOwnerInformation(@ApiParam(value = "(optional: password, name, nickname, gender, identity, is_graduated, major, student_number, phone_number, email)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) Owner owner, BindingResult bindingResult) throws Exception {

        Owner clear = new Owner();

        return new ResponseEntity<>(userService.updateOwnerInformation(StringXssChecker.xssCheck(owner, clear)), HttpStatus.CREATED);
    }


    @ApiOperation(value = "탈퇴", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001) \n\n" +
                                               "토큰의 유효시간이 만료되었을 때 (code: 100004) \n\n" +
                                               "토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> withdraw() throws Exception {
        userService.withdraw();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AuthExcept
    @RequestMapping(value = "/user/check/nickname/{nickname}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity checkUserNickName(@PathVariable(value = "nickname") String nickname) throws Exception {

        return new ResponseEntity<Map<String, Object>>(userService.checkUserNickName(nickname), HttpStatus.OK);
    }

    @AuthExcept
    @RequestMapping(value = "/user/authenticate", method = RequestMethod.GET)
    public String authenticate(@RequestParam(value = "auth_token") String auth_token) {
        boolean result = userService.authenticate(auth_token);

        if (!result) {
            return "mail/error_config";
        }

        return "mail/success_register_config";
    }

    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/user/find/password", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity changePasswordConfig(@ApiParam(value = "(required: account)", required = true) @RequestBody @Valid String account, BindingResult bindingResult, HttpServletRequest request) {

        // TODO: velocity template 에 인증 url에 들어갈 host를 넣기 위해 reigster에 url 데이터를 넘겼는데 추후 이 방법 없애고 plugin을 붙이는 방법으로 해결해보기
        // https://developer.atlassian.com/server/confluence/confluence-objects-accessible-from-velocity/

        return new ResponseEntity<Map<String, Object>>(userService.changePasswordConfig(account, getHost(request)), HttpStatus.CREATED);
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

    @AuthExcept
    @RequestMapping(value = "/user/change/password/config", method = RequestMethod.GET)
    public String changePasswordInput(@RequestParam(value = "reset_token") String resetToken, Model model) {
        boolean result = userService.changePasswordInput(resetToken);

        if (!result) {
            return "mail/error_config";
        }

        model.addAttribute("resetToken", resetToken);

        return "mail/change_password_config";
    }

    @AuthExcept
    @RequestMapping(value = "/user/change/password/submit", method = RequestMethod.POST)
    public String changePasswordAuthenticate(@RequestBody Map<String, Object> params, @RequestParam String reset_token) {
        String password = params.get("password").toString();
        boolean result = userService.changePasswordAuthenticate(password, reset_token);

        if (!result) {
            return "mail/error_config";
        }

        return "mail/success_change_password_config";
    }
}
