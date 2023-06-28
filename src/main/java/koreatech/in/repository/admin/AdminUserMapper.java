package koreatech.in.repository.admin;

import koreatech.in.domain.Criteria.StudentCriteria;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerIncludingShop;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.request.OwnersCondition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserMapper {
    User getUserById(@Param("id") Integer id);

    User getUndeletedUserByEmail(@Param("email") String email);
    void deleteUserLogicallyById(@Param("id") Integer id);
    void undeleteUserLogicallyById(@Param("id") Integer id);
    Integer getTotalCountOfUnauthenticatedOwnersByCondition(@Param("condition") OwnersCondition condition);
    List<OwnerIncludingShop> getUnauthenticatedOwnersByCondition(@Param("condition") OwnersCondition condition);
    void updateOwnerAuthorById(Integer ownerId);
    Owner getFullOwnerById(@Param("id") Integer id);
    List<Integer> getShopsIdByOwnerId(Integer id);
    List<Integer> getAttachmentsIdByOwnerId(Integer id);
    void updateOwner(@Param("owner") Owner owner);
    void updateUser(@Param("user") User user);
    Integer getCompanyRegistrationNumberUsedCount(@Param("company_registration_number") String company_registration_number,@Param("userId") Integer userId);
    Integer getNicknameUsedCount(@Param("nickname") String nickname, @Param("userId") Integer userId);
    Integer getEmailUsedCount(@Param("email") String email,@Param("userId") Integer userId);
    void updateOwnerGrantShopByOwnerId(Integer ownerId);
    Integer getTotalCountOfStudentsByCondition(@Param("criteria") StudentCriteria criteria);
    List<Student> getStudentsByCondition(@Param("begin") Integer begin, @Param("criteria") StudentCriteria criteria);
    Integer getTotalCountOfOwnersByCondition(@Param("condition") OwnersCondition condition);
    List<Owner> getOwnersByCondition(@Param("condition") OwnersCondition condition);
}
