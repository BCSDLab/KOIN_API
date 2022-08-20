package koreatech.in.service.admin;

import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.user.User;
import koreatech.in.domain.user.student.Student;

import java.util.Map;

public interface AdminUserService {
    Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception;

    User getUserForAdmin(int id) throws Exception;

    User createStudentForAdmin(Student student);

    User updateUserForAdmin(User user, int id);

    Map<String, Object> deleteUserForAdmin(int id);

    Authority createPermissionForAdmin(Authority authority, int userId);

    Authority getPermissionForAdmin(int userId);

    Authority updatePermissionForAdmin(Authority authority, int userId);

    Map<String, Object> deletePermissionForAdmin(int userId);

    Map<String, Object> loginForAdmin(User user) throws Exception;

    Map<String, Object> logoutForAdmin();

    Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception;

}
