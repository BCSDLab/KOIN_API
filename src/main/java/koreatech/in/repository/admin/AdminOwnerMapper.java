package koreatech.in.repository.admin;

import koreatech.in.domain.User.owner.Owner;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOwnerMapper {
    Owner getOwnerById(@Param("user_id") Integer user_id);
    void updateOwner(@Param("owner") Owner owner);
}
