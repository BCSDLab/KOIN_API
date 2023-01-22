package koreatech.in.repository.user;

import koreatech.in.domain.Authority;
import koreatech.in.domain.User.UserType;
import koreatech.in.domain.User.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Date;

@Repository
public interface UserMapper {
    User getAuthedUserById(@Param("id") Integer id);
    User getAuthedUserByAccount(@Param("account") String account);
    void updateLastLoggedAt(@Param("id") Integer id, @Param("currentDate") Date currentDate);
    void deleteUserLogicallyById(@Param("id") Integer id);
    User getUserByNickname(@Param("nickname") String nickname);
    void updateUser(@Param("user") User user);
    User getUserByAuthToken(@Param("authToken") String authToken);


    Integer isAccountAlreadyUsed(String account);
    Integer isNicknameAlreadyUsed(String nickname);
    void updateUserIsAuthed(@Param("id")Integer id, @Param("isAuth")Boolean isAuth);
    void updateResetTokenAndResetTokenExpiredTime(@Param("id") Integer id, @Param("resetToken") String resetToken, @Param("resetTokenExpiredTime") Date resetTokenExpiredTime);
    User getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);


    @Select("SELECT * FROM koin.admins WHERE USER_ID = #{id}")
    Authority getAuthorityByUserIdForAdmin(@Param("id") int id);
    void insertUser(User user) throws SQLException;
    User getUserById(@Param("id") int id);
    UserType getUserTypeById(@Param("id") int id);
    String getUserEmail(@Param("id") int id);
    Integer getTotalCount();
    User getUserByAccount(String account);
    User getUserByResetToken(String resetToken);

    // TODO: JOIN으로 처리할 수 있는지 알아보기.
//    @Select("SELECT * FROM koin.users u INNER JOIN koin.admins a ON u.id = a.user_id WHERE u.id = #{id}")
//    User findByUserWithAuthority(int id);
}
