package koreatech.in.service.admin;

import java.util.List;
import java.util.Map;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.UserCriteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.auth.TokenRefreshRequest;
import koreatech.in.dto.admin.auth.TokenRefreshResponse;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import koreatech.in.dto.admin.user.student.StudentResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;

public interface AdminUserService {
    LoginResponse login(LoginRequest request) throws Exception;

    void logout();

//    Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception;

    List<User> getUserListForAdmin(UserCriteria userCriteria) throws Exception;

    User getUserForAdmin(int id) throws Exception;

    StudentResponse getStudent(Integer userId) throws Exception;

    Student createStudentForAdmin(Student student);

    koreatech.in.dto.normal.user.student.response.StudentResponse updateStudentForAdmin(UpdateUserRequest updateUserRequest, int id);

    void deleteUser(Integer userId);

    void undeleteUser(Integer userId);

    Authority createPermissionForAdmin(Authority authority, int userId);

    Authority getPermissionForAdmin(int userId);

    Authority updatePermissionForAdmin(Authority authority, int userId);

    Map<String, Object> deletePermissionForAdmin(int userId);

    Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception;

    NewOwnersResponse getNewOwners(NewOwnersCondition condition);

    OwnerResponse getOwner(int ownerId);

    TokenRefreshResponse refresh(TokenRefreshRequest request);
}
