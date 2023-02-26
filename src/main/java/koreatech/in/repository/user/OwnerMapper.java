package koreatech.in.repository.user;

import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerAttachment;
import koreatech.in.domain.User.owner.OwnerAttachments;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerMapper {
    //    @Select("SELECT * FROM koin.users AS user LEFT JOIN koin.users_owners AS owner ON owner.user_id = user.id WHERE user.id = #{id}")
    Owner getOwnerById(Long id);

    @Update("UPDATE koin.users_owners AS owner SET owner.email = #{email} WHERE owner.user_id = #{id}")
    void updateOwner(Owner owner);

    void insertOwner(Owner owner);

    void safeDeleteOwner(Owner owner);

    void deleteOwner(Integer id);

    OwnerAttachment getOwnerAttachmentById(Long id);

    void deleteOwnerAttachmentLogically(Long id);

    void insertOwnerAttachments(OwnerAttachments attachments);

    //insertOwnerAttachments-foreach 에서, useGeneratedKeys 를 사용해도 id들을 채워주지 않아 반복문을 통해 OwnerAttachment 각각을 insert 함.
    void insertOwnerAttachment(OwnerAttachment attachment);

    void deleteOwnerAttachmentsLogically(OwnerAttachments ownerAttachments);
}
