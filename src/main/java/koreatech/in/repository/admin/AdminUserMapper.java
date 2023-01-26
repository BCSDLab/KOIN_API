package koreatech.in.repository.admin;

import koreatech.in.domain.Authority;
import koreatech.in.domain.User.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserMapper {
    User getUserById(@Param("id") Integer id);
    void createAdmin(@Param("admin") Authority admin);
}
