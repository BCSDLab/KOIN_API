package koreatech.in.repository;

import koreatech.in.domain.Authority;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("authorityMapper")
public interface AuthorityMapper {
    @Insert("INSERT INTO koin.admins (USER_ID, GRANT_USER, GRANT_CALLVAN, GRANT_LAND, GRANT_COMMUNITY, GRANT_SHOP, GRANT_VERSION, IS_DELETED, GRANT_MARKET, GRANT_CIRCLE, GRANT_LOST, GRANT_SURVEY, GRANT_BCSDLAB) " +
            "VALUES (#{user_id}, #{grant_user}, #{grant_callvan}, #{grant_land}, #{grant_community}, #{grant_shop}, #{grant_version}, #{is_deleted}, #{grant_market}, #{grant_circle}, #{grant_lost}, #{grant_survey}, #{grant_bcsdlab})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createAuthority(Authority authority);

    @Update("UPDATE koin.admins SET GRANT_USER=#{authority.grant_user}, GRANT_CALLVAN=#{authority.grant_callvan}, GRANT_LAND=#{authority.grant_land}, GRANT_COMMUNITY=#{authority.grant_community}, GRANT_SHOP=#{authority.grant_shop}, GRANT_VERSION=#{authority.grant_version}, IS_DELETED=#{authority.is_deleted}, GRANT_MARKET=#{authority.grant_market}, GRANT_CIRCLE=#{authority.grant_circle}, GRANT_LOST=#{authority.grant_lost}, GRANT_SURVEY=#{authority.grant_survey}, GRANT_BCSDLAB=#{authority.grant_bcsdlab} " +
            "WHERE USER_ID = #{userId}")
    void updateAuthorityByUserId(@Param("authority") Authority authority, @Param("userId") int userId);

    @Delete("DELETE FROM koin.admins WHERE USER_ID = #{userId}")
    void deleteAuthority(int userId);

    @Select("SELECT * FROM koin.admins WHERE USER_ID = #{user_id} AND IS_DELETED = 0")
    Authority getAuthorityByUserId(@Param("user_id") int user_id);

    @Select("SELECT * FROM koin.admins WHERE ID = #{id} AND IS_DELETED = 0")
    Authority getAuthority(@Param("id") int id);

    @Select("SELECT * FROM koin.admins ORDER BY USER_ID LIMIT #{cursor}, #{limit}")
    List<Authority> getAuthorityList(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.admins")
    Integer totalAuthorityCount();
}
