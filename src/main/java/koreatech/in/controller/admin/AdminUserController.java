package koreatech.in.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.Map;
import javax.validation.Valid;

import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.StudentCriteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.RequestDataInvalidResponse;
import koreatech.in.dto.admin.auth.TokenRefreshRequest;
import koreatech.in.dto.admin.auth.TokenRefreshResponse;
import koreatech.in.dto.admin.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.admin.user.owner.response.OwnerUpdateResponse;
import koreatech.in.dto.admin.user.owner.response.OwnersResponse;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;
import koreatech.in.dto.admin.user.request.OwnersCondition;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;
import koreatech.in.dto.admin.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.admin.user.student.response.StudentResponse;
import koreatech.in.dto.admin.user.student.response.StudentUpdateResponse;
import koreatech.in.dto.admin.user.student.response.StudentsResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.service.admin.AdminUserService;
import koreatech.in.util.StringXssChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "(Admin) User", description = "회원")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.USER)
@Controller
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;

    @ApiOperation("어드민 로그인")
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001) \n\n" +
                                               "이메일에 대한 회원 정보가 없을 때 (code: 101000) \n\n" +
                                               "비밀번호가 일치하지 않을 때 (code: 101001)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @AuthExcept
    @ParamValid
    @RequestMapping(value = "/admin/user/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request, BindingResult bindingResult) throws Exception {
        LoginResponse response = adminUserService.login(request);
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
        adminUserService.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AuthExcept
    @ApiOperation(
            value = "액세스 토큰 재발급"
            , notes = "- 어드민 권한만 허용"
            , authorizations = {@Authorization("Authorization")}
    )
    @ApiResponses({
            @ApiResponse(code = 401
                    , message = "잘못된 접근일 때 (code: 100001) \n\n"
                    + "토큰의 유효시간이 만료되었을 때 (code: 100004)"
                    , response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/user/refresh", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<TokenRefreshResponse> refresh(@ApiParam(required = true) @RequestBody TokenRefreshRequest request) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }
        //TODO Refresh Token을 통해 Admin인지 판단.
        TokenRefreshResponse tokenRefreshResponse = adminUserService.refresh(request);
        return new ResponseEntity<>(tokenRefreshResponse, HttpStatus.CREATED);
    }



    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<User> getUser(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<>(adminUserService.getUserForAdmin(id), HttpStatus.OK);
    }

    @ApiOperation(value = "특정 학생 조회", notes = "- 어드민 권한만 허용", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 조회한 회원이 존재하지 않을 때 (code: 101003)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 조회한 id가 학생이 아닐 때 (code: 101017)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/users/student/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StudentResponse> getStudent(@ApiParam(name = "id", required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<>(adminUserService.getStudent(id), HttpStatus.OK);
    }

    @ApiOperation(value = "사장님 권한 요청 허용", notes = "- 어드민 권한만 허용", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 회원이 존재하지 않을 때 (code: 101003) \n" +
                                               "- 상점이 존재하지 않을 때 (code:104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- id가 사장님이 아닐 때 (code: 101018) \n" +
                                               "- 인증된 회원일 때 (code: 101019)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/owner/{id}/authed", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> allowOwnerPermission(@ApiParam(value = "ownerId", required = true) @PathVariable("id") Integer ownerId) throws Exception {
        adminUserService.allowOwnerPermission(ownerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "학생 리스트 조회 (페이지네이션)", notes = "- 어드민 권한만 허용", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                    "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                    "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 유효하지 않은 페이지일 때 (code: 100002)" , response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/students", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StudentsResponse> getStudents(@ModelAttribute("criteria") StudentCriteria criteria) throws Exception {
        return new ResponseEntity<>(adminUserService.getStudents(criteria), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "특정 학생 정보 수정", authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                    "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                    "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 조회한 회원이 존재하지 않을 때 (code: 101003)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 조회한 id가 학생이 아닐 때 (code: 101017) \n" +
                    "- 중복된 닉네임일 때 (code: 101002)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 유효한 학번이 아닐 때 (code: 101015) \n" +
                    "- 유효한 전공이 아닐 때 (code: 101016) \n" +
                    "- 유효한 성별이 아닐 때 (code: 101018)", response = ExceptionResponse.class),
    })
    @RequestMapping(value = "/admin/users/student/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<StudentUpdateResponse> updateStudent(@ApiParam(value = "(optional: nickname, gender, major, student_number, phone_number)", required = false)
                                 @RequestBody @Valid StudentUpdateRequest request, BindingResult bindingResult, @ApiParam(value = "id", required = true) @PathVariable("id") int id) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }
        return new ResponseEntity<>(adminUserService.updateStudent(request, id), HttpStatus.OK);
    }

    @ApiOperation(value = "회원 삭제 (탈퇴 처리)", notes = "회원을 soft delete 합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 경우 (code: 100001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 경우 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 조회한 회원이 존재하지 않을 경우 (code: 101003)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 이미 삭제된(탈퇴한) 회원일 경우 (code: 101004)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/users/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteUser(@ApiParam(value = "회원 고유 id", required = true) @PathVariable("id") Integer userId) throws Exception {
        adminUserService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "회원 삭제 해제 (탈퇴 상태를 해제 처리)", notes = "회원의 soft delete 상태를 해제합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 경우 (code: 100001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 경우 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 조회한 회원이 존재하지 않을 경우 (code: 101003)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 탈퇴한 회원이 아닐 경우 (code: 101005) \n\n" +
                                               "- 탈퇴하지 않은 회원 중, 같은 아이디를 가지고 있는 회원이 있어서 탈퇴를 해제할 수 없는 경우 (code: 101006) \n\n" +
                                               "- 탈퇴하지 않은 회원 중, 같은 이메일을 가지고 있는 회원이 있어서 탈퇴를 해제할 수 없는 경우 (code: 101007)", response = ExceptionResponse.class),
    })
    @RequestMapping(value = "/admin/users/{id}/undelete", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> undeleteUser(@ApiParam(value = "회원 고유 id", required = true) @PathVariable("id") Integer userId) throws Exception {
        adminUserService.undeleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOff @ApiIgnore @Deprecated
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/student/users", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createUser(
            @ApiParam(value = "(required: email, password), " +
                    "(optional: name, nickname, student_number, major, is_graduated, phone_number, gender, identity, is_authed)", required = true)
            @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Student student,
            BindingResult bindingResult) throws Exception {

        return new ResponseEntity<User>(adminUserService.createStudentForAdmin(student), HttpStatus.CREATED);
    }

    @ApiOff @ApiIgnore @Deprecated
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getPermission(@ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(adminUserService.getPermissionForAdmin(userId), HttpStatus.OK);
    }

    @ApiOff @ApiIgnore @Deprecated
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updatePermission(@ApiParam(value = "(optional: grant_callvan, grant_user, grant_shop, grant_community, grant_version, grant_land, grant_market, is_deleted)", required = false) @RequestBody Authority authority, @ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(adminUserService.updatePermissionForAdmin(authority, userId), HttpStatus.CREATED);
    }

    @ApiOff @ApiIgnore @Deprecated
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createPermission(@ApiParam(value = "(optional: grant_callvan, grant_user, grant_shop, grant_community, grant_version, grant_land, grant_market, is_deleted)", required = false) @RequestBody Authority authority, @ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Authority>(adminUserService.createPermissionForAdmin(authority, userId), HttpStatus.CREATED);
    }

    @ApiOff @ApiIgnore @Deprecated
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/{userId}/permission", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deletePermission(@ApiParam(required = true) @PathVariable("userId") int userId) throws Exception {
        return new ResponseEntity<Map<String, Object>>(adminUserService.deletePermissionForAdmin(userId), HttpStatus.OK);
    }

    @ApiOff @ApiIgnore @Deprecated
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/users/permissions", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getPermissionList(@ApiParam(required = false) @RequestParam(value = "page", required = false, defaultValue="1") int page,
                                     @ApiParam(required = false) @RequestParam(value = "limit", required = false, defaultValue="10") int limit) throws Exception {
        return new ResponseEntity<Map<String, Object>>(adminUserService.getPermissionListForAdmin(page, limit), HttpStatus.OK);
    }

    @ApiOperation(value = "가입 신청한 사장님 리스트 조회 (페이지네이션)", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                    "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                    "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 유효하지 않은 페이지일 때 (code: 100002)" , response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/users/new-owners", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<NewOwnersResponse> getNewOwners(NewOwnersCondition condition) {
        condition.checkDataConstraintViolation();

        NewOwnersResponse response = adminUserService.getNewOwners(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "사장 리스트 조회 (페이지네이션)", notes = "- 어드민 권한만 허용", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                    "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                    "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 유효하지 않은 페이지일 때 (code: 100002)" , response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/users/owners", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<OwnersResponse> getOwners(@ModelAttribute("condition") OwnersCondition condition) {
        condition.checkDataConstraintViolation();
        
        return new ResponseEntity<>(adminUserService.getOwners(condition), HttpStatus.OK);
    }

    @ApiOperation(value = "특정 사장님 조회", notes = "- 어드민 권한만 허용", authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
        @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
            "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
            "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
        @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
        @ApiResponse(code = 404, message = "- 조회한 회원이 존재하지 않을 때 (code: 101003)", response = ExceptionResponse.class),
        @ApiResponse(code = 409, message = "- 조회한 회원의 신원이 사장님이 아닐 때 (code: 101018)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/users/owner/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<OwnerResponse> getOwner(@ApiParam(value = "owner_id", required = true) @PathVariable("id") int id) {
        return new ResponseEntity<>(adminUserService.getOwner(id), HttpStatus.OK);
    }

    @ApiOperation(value = "특정 사장님 수정", notes = "- 어드민 권한만 허용", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                    "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                    "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 조회한 회원이 존재하지 않을 때 (code: 101003)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 조회한 회원의 신원이 사장님이 아닐 때 (code: 101018) \n" +
                    "- 중복된 닉네임일 때 (code: 101002)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 유효한 성별이 아닐 때 (code: 101020) \n" +
                    "- 유효한 이메일이 아닐 때 (code: 101013) \n"  +
                    "- 유효한 사업자 번호가 아닐 때(code: 101021)", response = ExceptionResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/admin/users/owner/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<OwnerUpdateResponse> updateOwner(
            @ApiParam(value = "user_id", required = true) @PathVariable("id") Integer userId,
            @RequestBody @Valid OwnerUpdateRequest request, BindingResult bindingResult) throws Exception {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        return new ResponseEntity<>(adminUserService.updateOwner(userId, request),HttpStatus.OK);
    }
}
