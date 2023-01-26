package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.admin.user.request.CreateAuthorityRequest;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.service.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = "(Admin) User", description = "회원")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.USER)
@Controller
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;

    @ApiOperation("어드민 로그인")
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001) \n\n" +
                                               "아이디에 대한 회원 정보가 없을 때 (code: 101000) \n\n" +
                                               "비밀번호가 일치하지 않을 때 (code: 101001)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = ExceptionResponse.class)
    })
    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/admin/user/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request, BindingResult bindingResult) throws Exception {
        LoginResponse response = adminUserService.loginForAdmin(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // grant_user가 false인 어드민 유저여도 로그아웃은 가능하게 interceptor에서 처리해놓았음.
    @ApiOperation(value = "어드민 로그아웃", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001) \n\n" +
                                               "토큰의 유효시간이 만료되었을 때 (code: 100004) \n\n" +
                                               "토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "권한이 없을 때 (code: 100003)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/user/logout", method = RequestMethod.POST)
    public ResponseEntity<EmptyResponse> logout() {
        adminUserService.logoutForAdmin();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getUserList(@ModelAttribute("criteria") Criteria criteria) throws Exception {
        return new ResponseEntity<Map<String, Object>>(adminUserService.getUserListForAdmin(criteria), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getUser(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<User>(adminUserService.getUserForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/student/users", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createUser(
            @ApiParam(value = "(required: portal_account, password), " +
                    "(optional: name, nickname, student_number, major, is_graduated, phone_number, gender, identity, is_authed)", required = true)
            @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Student student,
            BindingResult bindingResult) throws Exception {

        return new ResponseEntity<User>(adminUserService.createStudentForAdmin(student), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/student/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateUser(@ApiParam(value = "(optional: portal_account, password, name, nickname, student_number, major, identity, is_graduated, phone_number, gender, is_authed)", required = false)
                              @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Student student,
                              BindingResult bindingResult, @ApiParam(required = true) @PathVariable("id") int id) throws Exception {

        return new ResponseEntity<User>(adminUserService.updateStudentForAdmin(student, id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteUser(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(adminUserService.deleteUserForAdmin(id), HttpStatus.OK);
    }


    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @ParamValid
    @RequestMapping(value = "/admin/users/{id}/permission", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createAuthority(
            @ApiParam(required = true) @PathVariable("id") Integer userId,
            @RequestBody(required = false) @Valid CreateAuthorityRequest request, BindingResult bindingResult) throws Exception {
        adminUserService.createAuthority(request, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getPermission(@ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(adminUserService.getPermissionForAdmin(userId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updatePermission(@ApiParam(value = "(optional: grant_callvan, grant_user, grant_shop, grant_community, grant_version, grant_land, grant_market, is_deleted)", required = false) @RequestBody Authority authority, @ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(adminUserService.updatePermissionForAdmin(authority, userId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deletePermission(@ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Map<String, Object>>(adminUserService.deletePermissionForAdmin(userId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/permissions", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getPermissionList(@ApiParam(required = false) @RequestParam(value = "page", required = false, defaultValue="1") int page,
                                     @ApiParam(required = false) @RequestParam(value = "limit", required = false, defaultValue="10") int limit) throws Exception {
        return new ResponseEntity<Map<String, Object>>(adminUserService.getPermissionListForAdmin(page, limit), HttpStatus.OK);
    }
}
