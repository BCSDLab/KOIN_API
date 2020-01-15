package koreatech.in.service;

import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.User.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception;

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

    Map<String,Object> updateInformation(User user) throws Exception;

    Map<String, Object> checkUserNickName(String nickname) throws Exception;

    Map<String, Object> login(User user) throws Exception;

    Map<String, Object> logout();
}
