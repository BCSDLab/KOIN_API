package koreatech.in.repository;

import koreatech.in.domain.Version.Version;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("versionMapper")
public interface VersionMapper {

    @Select("SELECT * FROM koin.versions WHERE TYPE = #{type} ORDER BY created_at DESC LIMIT 1")
    Version getVersion(@Param("type") String type);

    @Select("SELECT * FROM koin.versions")
    List<Version> getVersionsForAdmin();

    @Insert("INSERT INTO koin.versions (VERSION, TYPE)" +
            "VALUES (#{version}, #{type})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createVersionForAdmin(Version version);

    @Update("UPDATE koin.versions SET VERSION=#{version}, TYPE=#{type} WHERE ID = #{id}")
    void updateVersionForAdmin(Version version);

    @Delete("DELETE FROM koin.versions WHERE TYPE = #{type}")
    void deleteVersionForAdmin(@Param("type") String type);
}