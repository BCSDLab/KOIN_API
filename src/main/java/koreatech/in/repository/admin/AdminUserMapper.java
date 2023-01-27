package koreatech.in.repository.admin;

import koreatech.in.domain.User.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserMapper {
    User getUserById(@Param("id") Integer id);
    User getUndeletedUserByAccount(@Param("account") String account);
    User getUndeletedUserByEmail(@Param("email") String email);
    void deleteUserLogicallyById(@Param("id") Integer id);
    void undeleteUserLogicallyById(@Param("id") Integer id);
}
