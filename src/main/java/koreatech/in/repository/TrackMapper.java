package koreatech.in.repository;

import koreatech.in.domain.Homepage.TechStack;
import koreatech.in.domain.Homepage.Track;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "trackMapper")
public interface TrackMapper {
    Track getTrackByNameForAdmin(@Param("name") String name);



    @Select("SELECT * FROM koin.tracks WHERE is_deleted = 0")
    List<Track> getTracks();

    @Select("SELECT * FROM koin.tracks WHERE id = #{trackId} AND is_deleted = 0")
    Track getTrack(@Param(value = "trackId") Integer trackId);

    @Select("SELECT * FROM koin.tech_stacks AS t1, koin.tracks AS t2 WHERE t1.track_id = #{trackId} AND t1.is_deleted = 0 AND t2.id = #{trackId}")
    List<TechStack> getTrackTechStacks(@Param(value = "trackId") Integer trackId);

    // ===== ADMIN APIs =====
    @Select("SELECT * FROM koin.tracks")
    List<Track> getTracksForAdmin();
    @Select("SELECT * FROM koin.tracks WHERE ID = #{id}")
    Track getTrackForAdmin(@Param("id") int id);
    @Select("SELECT * FROM koin.tech_stacks AS t1, koin.tracks AS t2 WHERE t1.track_id = #{id} AND t2.id = #{id}")
    List<TechStack> getTrackTechStacksForAdmin(@Param("id") int id);

    @Insert("INSERT INTO koin.tracks(name, headcount, is_deleted) VALUES(#{name}, #{headcount}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createTrackForAdmin(Track track);

    @Update("UPDATE koin.tracks SET NAME = #{name}, HEADCOUNT = #{headcount}, IS_DELETED = #{is_deleted} WHERE ID = #{id}")
    void updateTrackForAdmin(Track track);

    @Delete("DELETE FROM koin.tracks WHERE ID = #{id}")
    void deleteTrackForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.tech_stacks WHERE ID = #{id}")
    TechStack getTechStackForAdmin(@Param("id") int id);

    @Insert("INSERT INTO koin.tech_stacks(image_url, name, description, track_id, is_deleted)" +
            " VALUES(#{image_url}, #{name}, #{description}, #{track_id}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createTechStackForAdmin(TechStack techStack);

    @Update("UPDATE koin.tech_stacks SET IMAGE_URL = #{image_url}, NAME = #{name}, DESCRIPTION = #{description}, " +
            "TRACK_ID = #{track_id}, IS_DELETED = #{is_deleted} WHERE ID = #{id}")
    void updateTechStackForAdmin(TechStack techStack);

    @Delete("DELETE FROM koin.tech_stacks WHERE ID = #{id}")
    void deleteTechStackForAdmin(@Param("id") int id);
}
