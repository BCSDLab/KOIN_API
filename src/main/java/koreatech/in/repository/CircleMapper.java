package koreatech.in.repository;

import koreatech.in.domain.Circle.Circle;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("circleMapper")
public interface CircleMapper {
    @Select("SELECT id, category, name, line_description, logo_url, background_img_url, professor, location, major_business, introduce_url FROM koin.circles WHERE IS_DELETED = 0 ORDER BY created_at LIMIT #{cursor}, #{limit}")
    List<Circle> getCircleList(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.circles ORDER BY created_at LIMIT #{cursor}, #{limit}")
    List<Circle> getCircleListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Insert("INSERT INTO koin.circles (CATEGORY, NAME, LINE_DESCRIPTION, LOGO_URL, DESCRIPTION, LINK_URLS, BACKGROUND_IMG_URL, PROFESSOR, LOCATION, " +
            "MAJOR_BUSINESS, INTRODUCE_URL, IS_DELETED) " +
            "VALUES (#{category}, #{name}, #{line_description}, #{logo_url}, #{description}, #{link_urls}, #{background_img_url}, #{professor}, #{location}, " +
            "#{major_business}, #{introduce_url}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createCircleForAdmin(Circle circle);

    @Select("SELECT id, category, name, line_description, logo_url, background_img_url, professor, location, major_business, introduce_url, description, link_urls FROM koin.circles WHERE ID = #{id} AND IS_DELETED = 0")
    Circle getCircle(@Param("id") int id);

    @Select("SELECT * FROM koin.circles WHERE ID = #{id}")
    Circle getCircleForAdmin(@Param("id") int id);

    @Update("UPDATE koin.circles SET CATEGORY=#{category}, NAME=#{name}, LINE_DESCRIPTION=#{line_description}, LOGO_URL=#{logo_url}, DESCRIPTION=#{description}, " +
            "LINK_URLS=#{link_urls}, BACKGROUND_IMG_URL=#{background_img_url}, PROFESSOR=#{professor}, LOCATION=#{location}, MAJOR_BUSINESS=#{major_business}, INTRODUCE_URL=#{introduce_url}, IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateCircleForAdmin(Circle circle);

    @Delete("Delete FROM koin.circles WHERE ID = #{id}")
    void deleteCircleForAdmin(@Param("id") int id);

    @Select("SELECT COUNT(*) AS TOTALCOUNT FROM circles WHERE IS_DELETED = 0")
    Integer totalCount();

    @Select("SELECT * FROM koin.circles WHERE NAME = #{name} AND IS_DELETED = 0")
    Circle getCircleByName(@Param("name") String name);
}
