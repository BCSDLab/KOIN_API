package koreatech.in.repository;

import koreatech.in.domain.Callvan.Company;
import koreatech.in.domain.Callvan.Participant;
import koreatech.in.domain.Callvan.Room;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallvanMapper {
    /*
        Callvan Company Mapper
    */
    @Insert("INSERT INTO koin.callvan_companies (NAME, PHONE, PAY_CARD, PAY_BANK, IS_DELETED) " +
            "VALUES (#{name}, #{phone}, #{pay_card}, #{pay_bank}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createCompanyForAdmin(Company company);

    @Update("UPDATE koin.callvan_companies SET NAME = #{name}, PHONE = #{phone}, PAY_CARD = #{pay_card}, PAY_BANK = #{pay_bank}, HIT = #{hit}, IS_DELETED = #{is_deleted} WHERE ID = #{id}")
    void updateCompany(Company company);

    @Delete("DELETE FROM koin.callvan_companies WHERE ID = #{id}")
    void deleteCompanyForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.callvan_companies WHERE IS_DELETED = 0 ORDER BY CREATED_AT DESC")
    List<Company> getCompanies();

    @Select("SELECT * FROM koin.callvan_companies WHERE ID = #{id} AND IS_DELETED = 0")
    Company getCompany(@Param("id") int id);

    @Select("SELECT * FROM koin.callvan_companies WHERE ID = #{id}")
    Company getCompanyForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.callvan_companies WHERE NAME = #{name}")
    Company getCompanyByNameForAdmin(@Param("name") String name);

    /*
        Callvan Room Mapper
    */
    @Select("SELECT * FROM koin.callvan_rooms WHERE IS_DELETED = 0 ORDER BY CREATED_AT DESC")
    List<Room> getRooms();

    @Insert("INSERT INTO koin.callvan_rooms (USER_ID, DEPARTURE_PLACE, DEPARTURE_DATETIME, ARRIVAL_PLACE, MAXIMUM_PEOPLE, CURRENT_PEOPLE) " +
            "VALUES (#{user_id}, #{departure_place}, #{departure_datetime}, #{arrival_place}, #{maximum_people}, #{current_people})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createRoom(Room room);

    @Select("SELECT * FROM koin.callvan_rooms WHERE ID = #{id} AND IS_DELETED = 0")
    Room getRoom(@Param("id") int id);

    @Update("UPDATE koin.callvan_rooms SET USER_ID=#{user_id}, DEPARTURE_PLACE=#{departure_place}, DEPARTURE_DATETIME=#{departure_datetime}, IS_DELETED=#{is_deleted}, " +
            "ARRIVAL_PLACE=#{arrival_place}, MAXIMUM_PEOPLE=#{maximum_people}, CURRENT_PEOPLE=#{current_people} WHERE ID = #{id}")
    void updateRoom(Room room);

    /*
        Callvan Participant Mapper
    */
    @Insert("INSERT INTO koin.callvan_participants (ROOM_ID, USER_ID) " +
            "VALUES (#{room_id}, #{user_id})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createParticipant(Participant participant);

    @Update("UPDATE koin.callvan_participants SET ROOM_ID = #{room_id}, USER_ID = #{user_id}, IS_DELETED = #{is_deleted} WHERE ID = #{id}")
    void updateParticipant(Participant participant);

    @Select("SELECT * FROM koin.callvan_participants WHERE ROOM_ID = #{room_id} AND IS_DELETED = 0")
    List<Participant> getParticipantsInRoom(@Param("room_id") int room_id);

    @Select("SELECT * FROM koin.callvan_participants WHERE ROOM_ID = #{room_id} AND USER_ID = #{user_id} AND IS_DELETED = 0")
    List<Participant> getParticipantInRoom(@Param("room_id") int room_id, @Param("user_id") int user_id);
}
