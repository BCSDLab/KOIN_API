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
    // TODO undeleted
    Integer isAccountAlreadyUsed(String account);
    Integer isNicknameAlreadyUsed(String nickname);
    // TODO need to search Not deleted and authed user
    <T extends User> Optional<T> getAuthedUserPasswordByAccount(String account);
    void updateUserIsAuth(Boolean isAuth);
    void updateResetTokenAndResetTokenExpiredTime(String resetToken, Date resetTokenExpiredTime);

    @Select("SELECT * FROM koin.users ORDER BY created_at LIMIT #{cursor}, #{limit}")
    <T extends User> List<T> getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.admins WHERE USER_ID = #{id}")
    Authority getAuthorityByUserIdForAdmin(@Param("id") int id);

    @Insert("INSERT IGNORE INTO koin.users (PORTAL_ACCOUNT, PASSWORD, NAME, NICKNAME, GENDER, IDENTITY, IS_GRADUATED, MAJOR, STUDENT_NUMBER, PHONE_NUMBER, AUTH_TOKEN, AUTH_EXPIRED_AT, IS_AUTHED, ANONYMOUS_NICKNAME, PROFILE_IMAGE_URL) " +
            "VALUES (#{portal_account}, #{password}, #{name}, #{nickname}, #{gender}, #{identity}, #{is_graduated}, #{major}, #{student_number}, #{phone_number}, #{auth_token}, #{auth_expired_at}, #{is_authed}, #{anonymous_nickname}, #{profile_image_url})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createUser(User user) throws SQLException;

    @Delete("DELETE FROM koin.users WHERE ID = #{id}")
    void deleteUser(@Param("id") int id);

    @Select("SELECT * FROM koin.users WHERE ID = #{id}")
    <T extends User> Optional<T> getUserById(@Param("id") int id);

    @Select("SELECT IDENTITY FROM koin.users WHERE ID = #{id}")
    UserType getUserTypeById(@Param("id") int id);

    @Select("SELECT EMAIL FROM koin.users WHERE USER_ID = #{id}")
    Optional<String> getUserEmail(@Param("id") int id);

    @Update("UPDATE koin.users SET PORTAL_ACCOUNT = #{portal_account}, PASSWORD = #{password}, NAME = #{name}, NICKNAME = #{nickname}, GENDER = #{gender}, IDENTITY = #{identity}, IS_GRADUATED = #{is_graduated}, MAJOR = #{major}, " +
            "STUDENT_NUMBER = #{student_number}, PHONE_NUMBER = #{phone_number}, ANONYMOUS_NICKNAME = #{anonymous_nickname}, AUTH_TOKEN = #{auth_token}, AUTH_EXPIRED_AT = #{auth_expired_at}, IS_AUTHED = #{is_authed}, RESET_TOKEN = #{reset_token}, RESET_EXPIRED_AT = #{reset_expired_at}, PROFILE_IMAGE_URL = #{profile_image_url} WHERE ID = #{id}")
    void updateUser(User user);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.users WHERE IS_DELETED = 0")
    Integer getTotalCount();

    @Select("SELECT * FROM koin.users WHERE PORTAL_ACCOUNT = #{portal_account} AND IS_DELETED = 0")
    <T extends User> Optional<T> getUserByAccount(String portalAccount);

    @Select("SELECT * FROM koin.users WHERE NICKNAME = #{nickname} AND IS_DELETED = 0")
    <T extends User> Optional<T> getUserByNickName(String nickname);

    @Select("SELECT * FROM koin.users WHERE AUTH_TOKEN = #{authToken}")
    <T extends User> Optional<T> getUserByAuthToken(String authToken);

    @Select("SELECT * FROM koin.users WHERE RESET_TOKEN = #{resetToken}")
    <T extends User> Optional<T> getUserByResetToken(String resetToken);

    // TODO: JOIN으로 처리할 수 있는지 알아보기.
//    @Select("SELECT * FROM koin.users u INNER JOIN koin.admins a ON u.id = a.user_id WHERE u.id = #{id}")
//    User findByUserWithAuthority(int id);
}
