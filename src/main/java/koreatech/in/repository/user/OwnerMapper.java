package koreatech.in.repository.user;

import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerShopAttachments;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerMapper {
    @Select("SELECT * FROM koin.users AS user LEFT JOIN koin.users_owners AS owner ON owner.user_id = user.id WHERE user.id = #{id}")
    Owner getOwnerById(Long id);
    @Update("UPDATE koin.users_owners AS owner SET owner.email = #{email} WHERE owner.user_id = #{id}")
    void updateOwner(Owner owner);
    void insertOwner(Owner owner);
    void safeDeleteOwner(Owner owner);
    void deleteOwner(Integer id);

    void insertOwnerShopAttachment(OwnerShopAttachments ownerShopAttachments);
}
