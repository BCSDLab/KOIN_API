package koreatech.in.service.admin;

import java.util.Map;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.dto.normal.user.response.StudentResponse;

public interface AdminUserService {
    LoginResponse loginForAdmin(LoginRequest request) throws Exception;

    void logoutForAdmin();

    Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception;

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
}
