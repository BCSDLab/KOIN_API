package koreatech.in.repository.user;

import java.sql.SQLException;
import java.util.List;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    User getAuthedUserById(@Param("id") Integer id);
    User getAuthedUserByEmail(@Param("email") String email);
    User getUserByEmail(@Param("email") String email);
    void deleteUser(@Param("user") User user);
    void undeleteUserLogicallyById(@Param("id") Integer id);

    User getUserByNickname(@Param("nickname") String nickname);
    void updateUser(@Param("user") User user);
    User getUserByAuthToken(@Param("authToken") String authToken);
    User getAuthedUserByResetToken(@Param("resetToken") String resetToken);
    void deleteRelationBetweenOwnerAndShop(@Param("ownerId") Integer ownerId);



//    Users getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);
    List<User> getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit, @Param("userType") String userType);

    void insertUser(User user) throws SQLException;

    Boolean isEmailAlreadyExist(EmailAddress emailAddress);
}
