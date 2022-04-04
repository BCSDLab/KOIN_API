package koreatech.in.repository;

//import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import koreatech.in.domain.Authority;
import koreatech.in.domain.User.Owner;
import koreatech.in.domain.User.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository("userMapper")
public interface UserMapper{
    @Select("SELECT * FROM koin.users ORDER BY created_at LIMIT #{cursor}, #{limit}")
    List<User> getUserListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.admins WHERE USER_ID = #{id}")
    Authority getAuthorityByUserIdForAdmin(@Param("id") int id);

    @Insert("INSERT IGNORE INTO koin.users (PORTAL_ACCOUNT, PASSWORD, NAME, NICKNAME, GENDER, IDENTITY, IS_GRADUATED, MAJOR, STUDENT_NUMBER, PHONE_NUMBER, AUTH_TOKEN, AUTH_EXPIRED_AT, IS_AUTHED, ANONYMOUS_NICKNAME, PROFILE_IMAGE_URL) " +
            "VALUES (#{portal_account}, #{password}, #{name}, #{nickname}, #{gender}, #{identity}, #{is_graduated}, #{major}, #{student_number}, #{phone_number}, #{auth_token}, #{auth_expired_at}, #{is_authed}, #{anonymous_nickname}, #{profile_image_url})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createUser(User user) throws SQLException;

    @Delete("DELETE FROM koin.users WHERE ID = #{id}")
    void deleteUser(@Param("id") int id);

    @Select("SELECT * FROM koin.users WHERE ID = #{id}")
    User getUser(@Param("id") int id);

    @Select("SELECT IDENTITY FROM koin.users WHERE ID = #{id}")
    Integer getUserIdentity(@Param("id") int id);

    @Select("SELECT * FROM koin.users AS user LEFT JOIN koin.users_owners AS owner ON owner.user_id = user.id WHERE user.id = #{id}")
    Owner getOwner(@Param("id") int id);

    @Select("SELECT EMAIL FROM koin.users_owners WHERE USER_ID = #{id}")
    String getOwnerEmail(@Param("id") int id);

    @Update("UPDATE koin.users SET PORTAL_ACCOUNT = #{portal_account}, PASSWORD = #{password}, NAME = #{name}, NICKNAME = #{nickname}, GENDER = #{gender}, IDENTITY = #{identity}, IS_GRADUATED = #{is_graduated}, MAJOR = #{major}, " +
            "STUDENT_NUMBER = #{student_number}, PHONE_NUMBER = #{phone_number}, ANONYMOUS_NICKNAME = #{anonymous_nickname}, AUTH_TOKEN = #{auth_token}, AUTH_EXPIRED_AT = #{auth_expired_at}, IS_AUTHED = #{is_authed}, RESET_TOKEN = #{reset_token}, RESET_EXPIRED_AT = #{reset_expired_at}, PROFILE_IMAGE_URL = #{profile_image_url} WHERE ID = #{id}")
    void updateUser(User user);

    @Update("UPDATE koin.users_owners AS owner SET owner.email = #{email} WHERE owner.user_id = #{id}")
    void updateOwner(Owner owner);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.users WHERE IS_DELETED = 0")
    Integer totalCount();

    @Select("SELECT * FROM koin.users WHERE PORTAL_ACCOUNT = #{portal_account} AND IS_DELETED = 0")
    User getUserByPortalAccount(String portal_account);

    @Select("SELECT * FROM koin.users WHERE NICKNAME = #{nickname} AND IS_DELETED = 0")
    User getUserByNickName(String nickname);

    @Select("SELECT * FROM koin.users WHERE AUTH_TOKEN = #{authToken}")
    User getUserByAuthToken(String authToken);

    @Select("SELECT * FROM koin.users WHERE RESET_TOKEN = #{resetToken}")
    User getUserByResetToken(String resetToken);

    // TODO: JOIN으로 처리할 수 있는지 알아보기.
//    @Select("SELECT * FROM koin.users u INNER JOIN koin.admins a ON u.id = a.user_id WHERE u.id = #{id}")
//    User findByUserWithAuthority(int id);
}
