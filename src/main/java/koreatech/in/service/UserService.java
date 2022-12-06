package koreatech.in.service;

import koreatech.in.domain.Authority;
import koreatech.in.domain.User.Owner;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UsersCondition;
import koreatech.in.dto.user.admin.UsersResponse;

import java.util.Map;

public interface UserService {
    UsersResponse getUserListForAdmin(UsersCondition condition) throws Exception;

    User getUserForAdmin(int id) throws Exception;

    User createUserForAdmin(User user);

    User updateUserForAdmin(User user, int id);

    Map<String, Object> deleteUserForAdmin(int id);

    Authority createPermissionForAdmin(Authority authority, int userId);

    Authority getPermissionForAdmin(int userId);

    Authority updatePermissionForAdmin(Authority authority, int userId);

    Map<String, Object> deletePermissionForAdmin(int userId);

    Map<String, Object> loginForAdmin(User user) throws Exception;

    Map<String, Object> logoutForAdmin();

    Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception;

    Map<String, Object> register(User user, String host) throws Exception;

    Boolean authenticate(String authToken);

    Map<String, Object> changePasswordConfig(User user, String host);

    Boolean changePasswordInput(String resetToken);

    Boolean changePasswordAuthenticate(String password, String resetToken);

    Map<String, Object> withdraw() throws Exception;

    User me() throws Exception;

    Map<String,Object> updateUserInformation(User user) throws Exception;

    Map<String,Object> updateOwnerInformation(Owner owner) throws Exception;

    Map<String, Object> checkUserNickName(String nickname) throws Exception;

    Map<String, Object> login(User user) throws Exception;

    Map<String, Object> logout();
}
