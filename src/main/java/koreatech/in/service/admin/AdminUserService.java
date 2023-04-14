package koreatech.in.service.admin;

import java.util.List;
import java.util.Map;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.UserCriteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.dto.normal.user.student.response.StudentResponse;

public interface AdminUserService {
    LoginResponse login(LoginRequest request) throws Exception;

    void logout();

//    Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception;

    List<User> getUserListForAdmin(UserCriteria userCriteria) throws Exception;

    User getUserForAdmin(int id) throws Exception;

    Student createStudentForAdmin(Student student);

    StudentResponse updateStudentForAdmin(UpdateUserRequest updateUserRequest, int id);

    void deleteUser(Integer userId);

    void undeleteUser(Integer userId);

    Authority createPermissionForAdmin(Authority authority, int userId);

    Authority getPermissionForAdmin(int userId);

    Authority updatePermissionForAdmin(Authority authority, int userId);

    Map<String, Object> deletePermissionForAdmin(int userId);

    Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception;

    NewOwnersResponse getNewOwners(NewOwnersCondition condition);

    OwnerResponse getOwner(int ownerId);
}
