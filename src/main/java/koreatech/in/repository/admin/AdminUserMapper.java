package koreatech.in.repository.admin;

import koreatech.in.domain.User.User;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserMapper {
    User getUserById(@Param("id") Integer id);

    User getUndeletedUserByEmail(@Param("email") String email);
    void deleteUserLogicallyById(@Param("id") Integer id);
    void undeleteUserLogicallyById(@Param("id") Integer id);
    Integer getTotalCountOfUnauthenticatedOwnersByCondition(@Param("condition") NewOwnersCondition condition);
    List<Owner> getUnauthenticatedOwnersByCondition(@Param("begin") Integer begin, @Param("condition") NewOwnersCondition condition);
    void updateOwnerAuthorById(Integer ownerId);
    Owner getFullOwnerById(@Param("id") Integer id);
    List<Integer> getShopsIdByOwnerId(Integer id);
    List<Integer> getAttachmentsIdByOwnerId(Integer id);
    void updateOwner(@Param("owner") Owner owner);
    void updateUser(@Param("user") User user);
    Integer isCompanyRegistrationNumberAlreadyUsed(String company_registration_number, Integer userId);
    Integer isNickNameAlreadyUsed(String nickname, Integer userId);
    Integer isEmailAlreadyUsed(String email, Integer userId);
    void updateOwnerGrantShopByOwnerId(Integer ownerId);
}
