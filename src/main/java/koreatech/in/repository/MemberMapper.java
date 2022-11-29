package koreatech.in.repository;

import koreatech.in.domain.Homepage.Member;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "memberMapper")
public interface MemberMapper {

    @Select("SELECT t1.id, t1.name, t1.student_number, t2.name AS track, t1.position, t1.email, t1.image_url, t1.is_deleted, t1.created_at, t1.updated_at" +
            " FROM koin.members AS t1 INNER JOIN koin.tracks AS t2 ON t1.track_id = t2.id WHERE t1.is_deleted = 0")
    List<Member> getMembers();

    @Select("SELECT t1.id, t1.name, t1.student_number, t2.name AS track, t1.position, t1.email, t1.image_url, t1.is_deleted, t1.created_at, t1.updated_at" +
            " FROM koin.members AS t1, koin.tracks AS t2 WHERE t1.track_id = #{trackId} AND t1.is_deleted = 0 AND t2.id = #{trackId}")
    List<Member> getTrackMembers(@Param(value = "trackId") Integer trackId);

    @Select("SELECT t1.id, t1.name, t1.student_number, t2.name AS track, t1.position, t1.email, t1.image_url, t1.is_deleted, t1.created_at, t1.updated_at" +
            " FROM koin.members AS t1, koin.tracks AS t2 WHERE t1.id = #{memberId} AND t1.is_deleted = 0 AND t2.id = t1.track_id")
    Member getMemberById(@Param(value = "memberId") Integer memberId);

    // ===== ADMIN APIs =====
    @Select("SELECT t1.id, t1.name, t1.student_number, t2.name AS track, t1.position, t1.email, t1.image_url, t1.is_deleted, t1.created_at, t1.updated_at" +
            " FROM koin.members AS t1 INNER JOIN koin.tracks AS t2 ON t1.track_id = t2.id")
    List<Member> getMembersForAdmin();

    @Select("SELECT t1.id, t1.name, t1.student_number, t2.name AS track, t1.position, t1.email, t1.image_url, t1.is_deleted, t1.created_at, t1.updated_at" +
            " FROM koin.members AS t1, koin.tracks AS t2 WHERE t1.track_id = #{id} AND t2.id = #{id}")
    List<Member> getTrackMembersForAdmin(@Param("id") int id);

    @Select("SELECT " +
                "m.id AS id, " +
                "m.`name` AS `name`, " +
                "m.student_number AS student_number, " +
                "t.`name` AS track, " +
                "m.`position` AS `position`, " +
                "m.`email` AS `email`, " +
                "m.image_url AS image_url, " +
                "m.is_deleted AS is_deleted, " +
                "m.created_at AS created_at, " +
                "m.updated_at AS updated_at " +
            "FROM koin.members m " +
                "LEFT JOIN koin.tracks t " +
                "ON m.track_id = t.id " +
            "WHERE m.id = #{id}")
    Member getMemberForAdmin(@Param(value = "id") int id);

    @Insert("INSERT INTO koin.members (name, student_number, track_id, position, email, image_url)" +
            " SELECT #{name}, #{student_number}, t2.id, #{position}, #{email}, #{image_url}" +
            " FROM koin.tracks AS t2 WHERE t2.name = #{track}")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createMemberForAdmin(Member member);

    @Update("UPDATE koin.members SET NAME = #{name}, STUDENT_NUMBER = #{student_number}, TRACK_ID = (SELECT id FROM koin.tracks WHERE name = #{track}), " +
            "POSITION = #{position}, email = #{email}, IMAGE_URL = #{image_url}, IS_DELETED = #{is_deleted} WHERE ID = #{id}")
    void updateMemberForAdmin(Member member);

    @Delete("DELETE FROM koin.members WHERE ID = #{id}")
    void deleteMemberForAdmin(@Param("id") int id);

    @Update("UPDATE koin.members SET is_deleted = 1 WHERE id = #{id}")
    void softDeleteMemberForAdmin(@Param("id") int id);

    @Update("UPDATE koin.members SET is_deleted = 0 WHERE id = #{id}")
    void undeleteMemberForAdmin(@Param("id") int id);
}
