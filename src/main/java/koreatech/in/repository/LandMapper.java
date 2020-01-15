package koreatech.in.repository;

import koreatech.in.domain.BokDuck.Land;
import koreatech.in.domain.BokDuck.LandComment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("landMapper")
public interface LandMapper {
    @Select("SELECT * FROM koin.lands WHERE NAME = #{name}")
    Land getLandByNameForAdmin(@Param("name") String name);

    @Select("SELECT id, name, internal_name, latitude, longitude, monthly_fee, charter_fee, room_type FROM koin.lands WHERE IS_DELETED = 0 ORDER BY ROOM_TYPE DESC, NAME")
    List<Land> getLandList();

    @Select("SELECT * FROM koin.lands WHERE ID = #{id} AND IS_DELETED = 0")
    Land getLand(@Param("id") int id);

    @Select("SELECT * FROM koin.lands WHERE ID = #{id}")
    Land getLandForAdmin(@Param("id") int id);

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

    @Insert("INSERT INTO koin.lands (NAME, INTERNAL_NAME, SIZE, ROOM_TYPE, LATITUDE, LONGITUDE, PHONE, IMAGE_URLS, ADDRESS, DESCRIPTION, FLOOR, DEPOSIT, MONTHLY_FEE, CHARTER_FEE, MANAGEMENT_FEE, " +
            "OPT_REFRIGERATOR, OPT_CLOSET, OPT_TV, OPT_MICROWAVE, OPT_GAS_RANGE, OPT_INDUCTION, OPT_WATER_PURIFIER, OPT_AIR_CONDITIONER, OPT_WASHER, OPT_BED, OPT_DESK, OPT_SHOE_CLOSET, OPT_ELECTRONIC_DOOR_LOCKS, " +
            "OPT_BIDET, OPT_VERANDA, OPT_ELEVATOR, IS_DELETED) " +
            "VALUES (#{name}, #{internal_name}, #{size}, #{room_type}, #{latitude}, #{longitude}, #{phone}, #{image_urls}, #{address}, #{description}, #{floor}, #{deposit}, #{monthly_fee}, #{charter_fee}, #{management_fee}, " +
            "#{opt_refrigerator}, #{opt_closet}, #{opt_tv}, #{opt_microwave}, #{opt_gas_range}, #{opt_induction}, #{opt_water_purifier}, #{opt_air_conditioner}, #{opt_washer}, #{opt_bed}, #{opt_desk}, #{opt_shoe_closet}, #{opt_electronic_door_locks}, #{opt_bidet}, #{opt_veranda}, #{opt_elevator}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createLandForAdmin(Land land);

    @Update("UPDATE koin.lands SET name=#{name}, internal_name=#{internal_name}, size=#{size}, room_type=#{room_type}, latitude=#{latitude}, longitude=#{longitude}, phone=#{phone}, image_urls=#{image_urls}, " +
            "address=#{address}, description=#{description}, floor=#{floor}, deposit=#{deposit}, monthly_fee=#{monthly_fee}, charter_fee=#{charter_fee}, management_fee=#{management_fee}, opt_refrigerator=#{opt_refrigerator}, opt_closet=#{opt_closet}, opt_tv=#{opt_tv}, " +
            "opt_microwave=#{opt_microwave}, opt_gas_range=#{opt_gas_range}, opt_induction=#{opt_induction}, opt_water_purifier=#{opt_water_purifier}, opt_air_conditioner=#{opt_air_conditioner}, opt_washer=#{opt_washer}, opt_bed=#{opt_bed}, opt_desk=#{opt_desk}, opt_shoe_closet=#{opt_shoe_closet}, " +
            "opt_electronic_door_locks=#{opt_electronic_door_locks}, opt_bidet=#{opt_bidet}, opt_veranda=#{opt_veranda}, opt_elevator=#{opt_elevator}, is_deleted=#{is_deleted} WHERE ID = #{id}")
    void updateLandForAdmin(Land land);

    @Delete("DELETE FROM koin.lands WHERE ID = #{id}")
    void deleteLandForAdmin(@Param("id") int id);

}
