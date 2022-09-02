package koreatech.in.controller.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.controller.user.dto.request.StudentRegisterRequest;
import koreatech.in.controller.user.dto.request.UserLoginRequest;
import koreatech.in.domain.user.owner.Owner;
import koreatech.in.domain.user.User;
import koreatech.in.domain.user.student.Student;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.service.user.UserService;
import koreatech.in.util.StringXssChecker;
import org.springframework.beans.factory.annotation.Autowired;
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

@Auth(role = Auth.Role.USER)
@Controller
public class UserController {
    @Inject
    private UserService userService;

    @Autowired
    StudentMapper studentMapper;

    @AuthExcept
    @ParamValid
    @ApiOperation(value = "(required: portal_account, password), (optional: name, nickname, gender, identity, is_graduated, major, student_number, phone_number)")
    @RequestMapping(value = "/user/student/register", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity studentRegister(
            @ApiParam(required = true) @RequestBody @Validated StudentRegisterRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest) throws Exception {

        // TODO: velocity template 에 인증 url에 들어갈 host를 넣기 위해 reigster에 url 데이터를 넘겼는데 추후 이 방법 없애고 plugin을 붙이는 방법으로 해결해보기
        // https://developer.atlassian.com/server/confluence/confluence-objects-accessible-from-velocity/

        StudentRegisterRequest clear = new StudentRegisterRequest();

        return new ResponseEntity<Map<String, Object>>(userService.StudentRegister((StudentRegisterRequest) StringXssChecker.xssCheck(request, clear), getHost(httpServletRequest)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/student/me", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity me() throws Exception {
        return new ResponseEntity<Object>(userService.getStudent(), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/me", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateStudentInformation(@ApiParam(value = "(optional: password, name, nickname, gender, identity, is_graduated, major, student_number, phone_number)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) Student student, BindingResult bindingResult) throws Exception {

        Student clear = new Student();

        return new ResponseEntity<>(userService.updateStudentInformation((Student) StringXssChecker.xssCheck(student, clear)), HttpStatus.CREATED);
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

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/me", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity withdraw() throws Exception {

        // TODO soft delete 방식으로 변경?
        return new ResponseEntity<Map<String, Object>>(userService.withdraw(), HttpStatus.OK);
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
    ResponseEntity changePasswordConfig(@ApiParam(value = "(required: portal_account)", required = true) @RequestBody @Valid String account, BindingResult bindingResult, HttpServletRequest request) {

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

    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public ResponseEntity login(@ApiParam(value = "(required: account, password)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) UserLoginRequest request, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Map<String, Object>>(userService.login(request.getAccount(), request.getPassword()), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public ResponseEntity logout() {
        return new ResponseEntity<Map<String, Object>>(userService.logout(), HttpStatus.OK);
    }
}
