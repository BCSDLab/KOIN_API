package koreatech.in.controller;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Authority;
import koreatech.in.domain.User.User;
import koreatech.in.service.UserService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Auth(role = Auth.Role.USER)
@Controller
public class UserController {
    @Inject
    private UserService userService;

    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity register(@ApiParam(value = "(required: portal_account, password), (optional: name, nickname, gender, identity, is_graduated, major, student_number, phone_number)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) User user, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        // TODO: default로 셋팅할 수 있는 방법 알아보기
        if (user.getIs_graduated() == null) {
            user.setIs_graduated(false);
        }

        if (user.getIs_authed() == null) {
            user.setIs_authed(false);
        }

        // TODO: velocity template 에 인증 url에 들어갈 host를 넣기 위해 reigster에 url 데이터를 넘겼는데 추후 이 방법 없애고 plugin을 붙이는 방법으로 해결해보기
        // https://developer.atlassian.com/server/confluence/confluence-objects-accessible-from-velocity/

        User clear = new User();

        return new ResponseEntity<Map<String, Object>>(userService.register((User)StringXssChecker.xssCheck(user, clear), getHost(request)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/me", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity me() throws Exception {
        return new ResponseEntity<Object>(userService.me(), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/me", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateInformation(@ApiParam(value = "(optional: password, name, nickname, gender, identity, is_graduated, major, student_number, phone_number)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) User user, BindingResult bindingResult) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        User clear = new User();

        return new ResponseEntity<Map<String,Object>>(userService.updateInformation((User)StringXssChecker.xssCheck(user, clear)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/me", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity withdraw() throws Exception {

        return new ResponseEntity<Map<String, Object>>(userService.withdraw(), HttpStatus.OK);
    }

    @AuthExcept
    @RequestMapping(value = "/user/check/nickname/{nickname}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity checkUserNickName(@PathVariable(value = "nickname") String nickname) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

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
    ResponseEntity changePasswordConfig(@ApiParam(value = "(required: portal_account)", required = true) @RequestBody @Valid User user, BindingResult bindingResult, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();

        // TODO: velocity template 에 인증 url에 들어갈 host를 넣기 위해 reigster에 url 데이터를 넘겼는데 추후 이 방법 없애고 plugin을 붙이는 방법으로 해결해보기
        // https://developer.atlassian.com/server/confluence/confluence-objects-accessible-from-velocity/

        return new ResponseEntity<Map<String, Object>>(userService.changePasswordConfig(user, getHost(request)), HttpStatus.CREATED);
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
    public ResponseEntity login(@ApiParam(value = "(required: portal_account, password)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) User user, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Map<String, Object>>(userService.login(user), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public ResponseEntity logout() {
        return new ResponseEntity<Map<String, Object>>(userService.logout(), HttpStatus.OK);
    }
}
