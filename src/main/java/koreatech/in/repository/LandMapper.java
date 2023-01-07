package koreatech.in.repository;

import koreatech.in.domain.BokDuck.Land;
import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.dto.admin.land.request.LandsCondition;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("landMapper")
public interface LandMapper {
    void createLandForAdmin(@Param("land") Land land);

    Land getLandByIdForAdmin(@Param("id") Integer id);

    Land getLandByNameForAdmin(@Param("name") String name);

    Integer getTotalCountByConditionForAdmin(@Param("condition") LandsCondition condition);

    List<Land> getLandsByConditionForAdmin(@Param("cursor") Integer cursor, @Param("condition") LandsCondition condition);

    void updateLandForAdmin(@Param("land") Land land);

    void deleteLandForAdmin(@Param("id") Integer id);

    void undeleteLandForAdmin(@Param("id") Integer id);


    @Select("SELECT " +
                "id, " +
                "`name`, " +
                "internal_name, " +
                "latitude, " +
                "longitude, " +
                "monthly_fee, " +
                "charter_fee, " +
                "room_type " +
            "FROM koin.lands " +
            "WHERE is_deleted = 0 " +
            "ORDER BY room_type DESC, `name`")
    List<Land> getLandList();

    @Select("SELECT * " +
            "FROM koin.lands " +
                "WHERE id = #{id} " +
                "AND is_deleted = 0")
    Land getLand(@Param("id") Integer id);

    @Select("SELECT * FROM koin.land_comments WHERE LAND_ID = #{land_id} AND IS_DELETED = 0")
    List<LandComment> getLandCommentList(@Param("land_id") int land_id);

    @Select("SELECT * FROM koin.land_comments WHERE LAND_ID = #{land_id} AND USER_ID = #{user_id} AND IS_DELETED = 0")
    LandComment getLandComment(@Param("land_id") int land_id, @Param("user_id") int user_id);

    @Insert("INSERT INTO koin.land_comments (LAND_ID, CONTENT, USER_ID, NICKNAME, SCORE)" +
            "VALUES (#{land_id}, #{content}, #{user_id}, #{nickname}, #{score})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createLandComment(LandComment landComment);

    @Update("UPDATE koin.land_comments SET LAND_ID=#{land_id}, CONTENT=#{content}, USER_ID=#{user_id}, NICKNAME=#{nickname}, IS_DELETED=#{is_deleted}, SCORE=#{score} WHERE ID = #{id}")
    void updateLandComment(LandComment landComment);
}
