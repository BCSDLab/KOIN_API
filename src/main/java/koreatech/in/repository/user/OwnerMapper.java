package koreatech.in.repository.user;

import koreatech.in.domain.user.owner.Owner;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerMapper {
    @Select("SELECT * FROM koin.users AS user LEFT JOIN koin.users_owners AS owner ON owner.user_id = user.id WHERE user.id = #{id}")
    Optional<Owner> getOwnerById(Long id);
    @Update("UPDATE koin.users_owners AS owner SET owner.email = #{email} WHERE owner.user_id = #{id}")
    void updateOwner(Owner owner);
    void insertOwner(Owner owner);
    void safeDeleteOwner(Owner owner);
    void deleteOwner(Integer id);

}
