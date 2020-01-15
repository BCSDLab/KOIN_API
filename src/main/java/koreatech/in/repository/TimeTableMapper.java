package koreatech.in.repository;

import koreatech.in.domain.TimeTable.Lecture;
import koreatech.in.domain.TimeTable.TimeTable;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository("timeTableMapper")
public interface TimeTableMapper {

    @Select("SELECT * FROM koin.lectures WHERE semester_date = #{semester_date}")
    @Results({@Result(property = "lecture_class", column = "class")})
    ArrayList<Lecture> getLectureList(@Param("semester_date") String semester_date);

    @Select("SELECT id, user_id, semester_id, code, class_title, class_time, class_place, professor, grades, lecture_class, target, regular_number, design_score, department, memo" +
            " FROM koin.timetables WHERE user_id = #{user_id} AND semester_id = #{semester_id} AND is_deleted = 0")
    ArrayList<TimeTable> getTimeTableList(@Param("user_id") int user_id, @Param("semester_id") int semester_id);

    @Select("SELECT * FROM koin.timetables WHERE ID = #{id} AND IS_DELETED = 0")
    TimeTable getTimeTable(@Param("id") int id);

    @Insert("INSERT INTO koin.timetables(user_id, semester_id, code, class_title, class_time, class_place, professor, grades, lecture_class, target, regular_number, design_score, department, memo)" +
            " VALUES (#{user_id}, #{semester_id}, #{code}, #{class_title}, #{class_time}, #{class_place}, #{professor}, #{grades}, #{lecture_class}, #{target}, #{regular_number}, #{design_score}, #{department}, #{memo})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createTimeTable(TimeTable timeTable);

    // UPDATE via "user_id", "class_title", "semester"
    @Update("UPDATE koin.timetables SET CODE=#{code}, CLASS_TITLE=#{class_title}, CLASS_TIME=#{class_time}, CLASS_PLACE=#{class_place}, PROFESSOR=#{professor}, GRADES=#{grades}, LECTURE_CLASS=#{lecture_class}, TARGET=#{target}, REGULAR_NUMBER=#{regular_number}, DESIGN_SCORE=#{design_score}, DEPARTMENT=#{department}, MEMO=#{memo}, IS_DELETED=#{is_deleted} " +
                        "WHERE ID = #{id}")
    void updateTimeTable(TimeTable timeTable);

    @Update("UPDATE koin.timetables AS t SET is_deleted = 1 WHERE user_id = #{user_id} AND semester_id IN (SELECT id FROM koin.semester WHERE semester = #{semester})")
    void deleteTimeTableAll(@Param("user_id") int user_id, @Param("semester") String semester);

    // koin.semester 테이블에서 확인, timetable API용
    @Select("SELECT COUNT(*) FROM koin.semester WHERE semester = #{semester}")
    Integer checkSemesterExists(@Param("semester") String semester);

    // koin.semester 테이블에서 확인, timetable API용
    @Select("SELECT COUNT(*) FROM koin.semester WHERE id = #{semester_id}")
    Integer checkSemesterExistsById(@Param("semester_id") int semester_id);

    // koin.lectures 테이블에서 직접 확인, lectures API용
    @Select("SELECT COUNT(*) FROM koin.lectures WHERE semester_date = #{semester_date}")
    Integer checkSemesterDateExists(@Param("semester_date") String semester_date);

    @Select("SELECT id FROM koin.semester WHERE semester = #{semester}")
    Integer getSemesterId(@Param("semester") String semester);
}