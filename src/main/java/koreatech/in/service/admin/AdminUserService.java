package koreatech.in.service.admin;

import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.request.LoginRequest;

import java.util.Map;

public interface AdminUserService {
    Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception;

    User getUserForAdmin(int id) throws Exception;

    Student createStudentForAdmin(Student student);

    Student updateStudentForAdmin(Student user, int id);

    Map<String, Object> deleteUserForAdmin(int id);

    Authority createPermissionForAdmin(Authority authority, int userId);

    Authority getPermissionForAdmin(int userId);

    Authority updatePermissionForAdmin(Authority authority, int userId);

    Map<String, Object> deletePermissionForAdmin(int userId);

    Map<String, Object> loginForAdmin(LoginRequest request) throws Exception;

    Map<String, Object> logoutForAdmin();

    Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception;

}
