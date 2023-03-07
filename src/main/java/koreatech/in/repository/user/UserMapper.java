package koreatech.in.repository.user;

import java.sql.SQLException;
import java.util.Date;
import koreatech.in.domain.Authority;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserType;
import koreatech.in.domain.User.Users;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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


//    Integer isAccountAlreadyUsed(String account);
    Integer isNicknameAlreadyUsed(String nickname);
    void updateUserIsAuthed(@Param("id")Integer id, @Param("isAuth")Boolean isAuth);
    void updateResetTokenAndResetTokenExpiredTime(@Param("id") Integer id, @Param("resetToken") String resetToken, @Param("resetTokenExpiredTime") Date resetTokenExpiredTime);
    Users getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);


    @Select("SELECT * FROM koin.admins WHERE USER_ID = #{id}")
    Authority getAuthorityByUserIdForAdmin(@Param("id") int id);
    void insertUser(User user) throws SQLException;
    User getUserById(@Param("id") int id);

    UserType getUserTypeById(@Param("id") int id);
    String getUserEmail(@Param("id") int id);
    Integer getTotalCount();

    // TODO: JOIN으로 처리할 수 있는지 알아보기.
//    @Select("SELECT * FROM koin.users u INNER JOIN koin.admins a ON u.id = a.user_id WHERE u.id = #{id}")
//    User findByUserWithAuthority(int id);

    Boolean isEmailAlreadyExist(EmailAddress emailAddress);
}
