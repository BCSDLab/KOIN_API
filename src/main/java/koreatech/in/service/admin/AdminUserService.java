package koreatech.in.service.admin;

import java.util.Map;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.StudentCriteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
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
import koreatech.in.dto.admin.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.admin.user.student.response.StudentResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;
import koreatech.in.dto.admin.user.student.response.StudentUpdateResponse;
import koreatech.in.dto.admin.user.student.response.StudentsResponse;

public interface AdminUserService {
    LoginResponse login(LoginRequest request) throws Exception;

    void logout();

//    Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception;

    StudentsResponse getStudents(StudentCriteria criteria) throws Exception;

    User getUserForAdmin(int id) throws Exception;

    StudentResponse getStudent(Integer userId) throws Exception;

    Student createStudentForAdmin(Student student);

    StudentUpdateResponse updateStudent(StudentUpdateRequest updateUserRequest, int id);

    void deleteUser(Integer userId);

    void undeleteUser(Integer userId);

    Authority createPermissionForAdmin(Authority authority, int userId);

    Authority getPermissionForAdmin(int userId);

    Authority updatePermissionForAdmin(Authority authority, int userId);

    Map<String, Object> deletePermissionForAdmin(int userId);

    Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception;

    void allowOwnerPermission(Integer ownerId, Integer shopId);

    OwnerResponse getOwner(int ownerId);

    OwnerUpdateResponse updateOwner(Integer userId, OwnerUpdateRequest request);

    TokenRefreshResponse refresh(TokenRefreshRequest request);

    OwnersResponse getOwners(OwnersCondition criteria);

    NewOwnersResponse getNewOwners(NewOwnersCondition condition);

}
