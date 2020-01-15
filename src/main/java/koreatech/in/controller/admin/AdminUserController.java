package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.User.User;
import koreatech.in.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.USER)
@Controller
public class AdminUserController {
    @Inject
    private UserService userService;

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getUserList(@ModelAttribute("criteria") Criteria criteria) throws Exception {
        return new ResponseEntity<Map<String, Object>>(userService.getUserListForAdmin(criteria), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getUser(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<User>(userService.getUserForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createUser(@ApiParam(value = "(required: portal_account, password, identity), (optional: name, nickname, student_number, major, is_graduated, phone_number, gender, is_authed)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) User user, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<User>(userService.createUserForAdmin(user), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateUser(@ApiParam(value = "(optional: portal_account, password, name, nickname, student_number, major, identity, is_graduated, phone_number, gender, is_authed)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) User user, BindingResult bindingResult, @ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<User>(userService.updateUserForAdmin(user, id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteUser(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(userService.deleteUserForAdmin(id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getPermission(@ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(userService.getPermissionForAdmin(userId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updatePermission(@ApiParam(value = "(optional: grant_callvan, grant_user, grant_shop, grant_community, grant_version, grant_land, grant_market, is_deleted)", required = false) @RequestBody Authority authority, @ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(userService.updatePermissionForAdmin(authority, userId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createPermission(@ApiParam(value = "(optional: grant_callvan, grant_user, grant_shop, grant_community, grant_version, grant_land, grant_market, is_deleted)", required = false) @RequestBody Authority authority, @ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(userService.createPermissionForAdmin(authority, userId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deletePermission(@ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Map<String, Object>>(userService.deletePermissionForAdmin(userId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/permissions", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getPermissionList(@ApiParam(required = false) @RequestParam(value = "page", required = false, defaultValue="1") int page,
                                     @ApiParam(required = false) @RequestParam(value = "limit", required = false, defaultValue="10") int limit) throws Exception {
        return new ResponseEntity<Map<String, Object>>(userService.getPermissionListForAdmin(page, limit), HttpStatus.OK);
    }

    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/admin/user/login", method = RequestMethod.POST)
    public ResponseEntity login(@ApiParam(value = "(required: portal_account, password)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) User user, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Map<String, Object>>(userService.loginForAdmin(user), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/user/logout", method = RequestMethod.POST)
    public ResponseEntity logout() {
        return new ResponseEntity<Map<String, Object>>(userService.logoutForAdmin(), HttpStatus.OK);
    }
}
