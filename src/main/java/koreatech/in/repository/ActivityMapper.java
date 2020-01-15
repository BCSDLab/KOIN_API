package koreatech.in.repository;

import koreatech.in.domain.Homepage.Activity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("activityMapper")
public interface ActivityMapper {
    @Select("SELECT * FROM koin.activities WHERE is_deleted = 0 AND year(date) = #{year}")
    List<Activity> getActivityListByYear(@Param("year") String year);
    @Select("SELECT * FROM koin.activities WHERE is_deleted = 0")
    List<Activity> getActivityList();

    // ===== ADMIN APIs =====
    @Select("SELECT * FROM koin.activities WHERE year(date) = #{year}")
    List<Activity> getActivityListByYearForAdmin(@Param("year") String year);
    @Select("SELECT * FROM koin.activities")
    List<Activity> getActivityListForAdmin();
    @Select("SELECT * FROM koin.activities WHERE id = #{id}")
    Activity getActivityForAdmin(@Param("id") int id);

    @Insert("INSERT INTO koin.activities(title, description, image_urls, date, is_deleted)" +
            " VALUES(#{title}, #{description}, #{image_urls}, #{date}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createActivityForAdmin(Activity activity);

    @Update("UPDATE koin.activities SET TITLE = #{title}, DESCRIPTION = #{description}, IMAGE_URLS = #{image_urls}, DATE = #{date}, is_deleted = #{is_deleted} WHERE ID = #{id}")
    void updateActivityForAdmin(Activity activity);

    @Delete("DELETE FROM koin.activities WHERE ID = #{id}")
    void deleteActivityForAdmin(@Param("id") int id);
}