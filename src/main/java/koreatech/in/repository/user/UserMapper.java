package koreatech.in.repository.user;

import koreatech.in.domain.Authority;
import koreatech.in.domain.user.UserType;
import koreatech.in.domain.user.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Date;

@Repository
public interface UserMapper{
    Integer isAccountAlreadyUsed(String account);
    Integer isNicknameAlreadyUsed(String nickname);
    User getAuthedUserByAccount(String account);
    void updateUserIsAuthed(@Param("id")Integer id, @Param("isAuth")Boolean isAuth);
    void updateResetTokenAndResetTokenExpiredTime(Integer id, String resetToken, Date resetTokenExpiredTime);
    User getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);
    Authority getAuthorityByUserIdForAdmin(@Param("id") int id);
    void insertUser(User user) throws SQLException;
    void deleteUser(@Param("id") int id);
    User getUserById(@Param("id") int id);
    UserType getUserTypeById(@Param("id") int id);
    String getUserEmail(@Param("id") int id);
    void updateUser(User user);
    void updateLastLoggedAt(@Param("id")int id, @Param("currentDate") Date currentDate);
    Integer getTotalCount();
    User getUserByAccount(String account);
    User getUserByNickName(String nickname);
    User getUserByAuthToken(String authToken);
    User getUserByResetToken(String resetToken);

    // TODO: JOIN으로 처리할 수 있는지 알아보기.
//    @Select("SELECT * FROM koin.users u INNER JOIN koin.admins a ON u.id = a.user_id WHERE u.id = #{id}")
//    User findByUserWithAuthority(int id);
}
