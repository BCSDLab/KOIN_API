package koreatech.in.repository.user;

//import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import koreatech.in.domain.Authority;
import koreatech.in.domain.user.UserType;
import koreatech.in.domain.user.owner.Owner;
import koreatech.in.domain.user.User;
import koreatech.in.domain.user.student.Student;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository("userMapper")
public interface UserMapper{
    Integer isAccountAlreadyUsed(String account);
    Integer isNicknameAlreadyUsed(String nickname);
    <T extends User> Optional<T> getAuthedUserByAccount(String account);
    void updateUserIsAuthed(Integer id, Boolean isAuth);
    void updateResetTokenAndResetTokenExpiredTime(Integer id, String resetToken, Date resetTokenExpiredTime);
    <T extends User> List<T> getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.admins WHERE USER_ID = #{id}")
    Authority getAuthorityByUserIdForAdmin(@Param("id") int id);

    void insertUser(User user) throws SQLException;
    void deleteUser(@Param("id") int id);
    <T extends User> Optional<T> getUserById(@Param("id") int id);
    UserType getUserTypeById(@Param("id") int id);
    Optional<String> getUserEmail(@Param("id") int id);
    void updateUser(User user);
    Integer getTotalCount();
    <T extends User> Optional<T> getUserByAccount(String portalAccount);
    <T extends User> Optional<T> getUserByNickName(String nickname);
    <T extends User> Optional<T> getUserByAuthToken(String authToken);
    <T extends User> Optional<T> getUserByResetToken(String resetToken);

    // TODO: JOIN으로 처리할 수 있는지 알아보기.
//    @Select("SELECT * FROM koin.users u INNER JOIN koin.admins a ON u.id = a.user_id WHERE u.id = #{id}")
//    User findByUserWithAuthority(int id);
}
