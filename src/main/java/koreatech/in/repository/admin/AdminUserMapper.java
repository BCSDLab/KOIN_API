package koreatech.in.repository.admin;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import koreatech.in.domain.User.User;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;

@Repository
public interface AdminUserMapper {
    User getUserById(@Param("id") Integer id);
    User getUndeletedUserByEmail(@Param("email") String email);
    void deleteUserLogicallyById(@Param("id") Integer id);
    void undeleteUserLogicallyById(@Param("id") Integer id);
    Integer getTotalCountOfUnauthenticatedOwnersByCondition(@Param("condition") NewOwnersCondition condition);
    List<Owner> getUnauthenticatedOwnersByCondition(@Param("begin") Integer begin, @Param("condition") NewOwnersCondition condition);
    Owner getFullOwnerById(@Param("id") Integer id);
}
