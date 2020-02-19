package koreatech.in.repository;

import koreatech.in.domain.MarketPlace.Item;
import koreatech.in.domain.MarketPlace.ItemComment;
import koreatech.in.domain.MarketPlace.ItemViewLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("marketPlaceMapper")
public interface MarketPlaceMapper {
    //TODO : select 조건 컬럼이 변경될때마다 매퍼 쿼리를 생성해야되는지 알아보기

    @Insert("INSERT INTO koin.items (TYPE, TITLE, CONTENT, USER_ID, NICKNAME, STATE, PRICE, PHONE, IS_PHONE_OPEN, THUMBNAIL, IP, IS_DELETED) " +
            "VALUES (#{type}, #{title}, #{content}, #{user_id}, #{nickname}, #{state}, #{price}, #{phone}, #{is_phone_open}, #{thumbnail}, #{ip}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createItemForAdmin(Item item);

    @Select("SELECT id, type, title, content, nickname, state, price, phone, is_phone_open, created_at, updated_at, thumbnail, hit FROM koin.items WHERE IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Item> getItemList(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.items ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Item> getItemListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT id, type, title, content, nickname, state, price, phone, is_phone_open, created_at, updated_at, thumbnail, hit FROM koin.items WHERE IS_DELETED = 0 AND TYPE = #{type} ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Item> getItemListByType(@Param("type") int type, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.items WHERE TYPE = #{type} ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Item> getItemListByTypeForAdmin(@Param("type") int type, @Param("cursor") int cursor, @Param("limit") int limit);

    @Insert("INSERT INTO koin.items (TYPE, TITLE, CONTENT, USER_ID, NICKNAME, STATE, PRICE, PHONE, IS_PHONE_OPEN, THUMBNAIL, IP) " +
            "VALUES (#{type}, #{title}, #{content}, #{user_id}, #{nickname}, #{state}, #{price}, #{phone}, #{is_phone_open}, #{thumbnail}, #{ip})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createItem(Item item);

    @Select("SELECT * FROM koin.items WHERE ID = #{id} AND IS_DELETED = 0")
    Item getItem(@Param("id") int id);

    @Select("SELECT * FROM koin.items WHERE ID = #{id}")
    Item getItemForAdmin(@Param("id") int id);

    @Update("UPDATE koin.items SET TITLE=#{title}, CONTENT=#{content}, USER_ID=#{user_id}, NICKNAME=#{nickname}, STATE=#{state}, PRICE=#{price}, PHONE=#{phone}, " +
            "IS_PHONE_OPEN=#{is_phone_open}, THUMBNAIL=#{thumbnail}, IP=#{ip}, IS_DELETED=#{is_deleted}, HIT=#{hit} WHERE ID = #{id}")
    void updateItem(Item item);

    @Delete("DELETE FROM koin.items WHERE ID = #{id}")
    void deleteItemForAdmin(@Param("id") int id);

    @Insert("INSERT INTO koin.item_comments (ITEM_ID, CONTENT, USER_ID, NICKNAME, IS_DELETED)" +
            "VALUES (#{item_id}, #{content}, #{user_id}, #{nickname}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createItemComment(ItemComment itemcomment);

    @Insert("INSERT INTO koin.item_comments (ITEM_ID, CONTENT, USER_ID, NICKNAME)" +
            "VALUES (#{item_id}, #{content}, #{user_id}, #{nickname})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createItemCommentForAdmin(ItemComment itemcomment);

    @Select("SELECT * FROM koin.item_comments WHERE ITEM_ID = #{item_id} AND IS_DELETED = 0")
    List<ItemComment> getItemCommentList(@Param("item_id") int id);

    @Select("SELECT * FROM koin.item_comments WHERE ITEM_ID = #{item_id}")
    List<ItemComment> getItemCommentListForAdmin(@Param("item_id") int id);

    @Select("SELECT * FROM koin.item_comments WHERE ID = #{comment_id} AND ITEM_ID = #{item_id} AND IS_DELETED = 0")
    ItemComment getItemComment(@Param("item_id") int item_id, @Param("comment_id") int comment_id);

    @Select("SELECT * FROM koin.item_comments WHERE ID = #{comment_id} AND ITEM_ID = #{item_id}")
    ItemComment getItemCommentForAdmin(@Param("item_id") int item_id, @Param("comment_id") int comment_id);

    @Update("UPDATE koin.item_comments SET ITEM_ID=#{item_id}, CONTENT=#{content}, USER_ID=#{user_id}, NICKNAME=#{nickname}, IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateItemComment(ItemComment itemcomment);

    @Delete("DELETE FROM koin.item_comments WHERE ID = #{id}")
    void deleteItemCommentForAdmin(@Param("id") int id);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.items WHERE IS_DELETED = 0")
    Integer totalItemCount();

    @Select("SELECT COUNT(*) AS totalCount FROM koin.items")
    Integer totalItemCountForAdmin();

    @Select("SELECT COUNT(*) AS totalCount FROM koin.items WHERE IS_DELETED = 0 AND TYPE=#{type}")
    Integer totalItemCountByType(@Param("type") int type);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.items WHERE TYPE=#{type} AND USER_ID = #{user_id} AND IS_DELETED = 0")
    Integer totalMyItemCountByType(@Param("type") int type, @Param("user_id") int user_id);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.items WHERE TYPE=#{type}")
    Integer totalItemCountByTypeForAdmin(@Param("type") int type);

    @Select("SELECT id, type, nickname, title, price, state, thumbnail, created_at FROM koin.items WHERE TYPE = #{type} AND USER_ID = #{user_id} AND IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Item> getMyItemList(@Param("cursor") int cursor, @Param("limit") int limit, @Param("type") int type, @Param("user_id") int user_id);

    @Select("SELECT * FROM koin.item_view_logs WHERE ITEM_ID = #{item_id} AND USER_ID = #{user_id} ORDER BY ID DESC LIMIT 1")
    ItemViewLog getViewLog(@Param("item_id") int lost_item_id, @Param("user_id") int user_id);

    @Insert("INSERT INTO koin.item_view_logs (ITEM_ID, USER_ID, EXPIRED_AT, IP)" +
            "VALUES (#{item_id}, #{user_id}, #{expired_at}, #{ip})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createViewLog(ItemViewLog viewLog);

    @Update("UPDATE koin.item_view_logs SET ITEM_ID=#{item_id}, USER_ID=#{user_id}, EXPIRED_AT=#{expired_at}, IP=#{ip} WHERE ID = #{id}")
    void updateViewLog(ItemViewLog viewLog);

    @Update("UPDATE koin.items SET HIT = HIT + 1 WHERE IS_DELETED = 0 AND ID = #{id}")
    void increaseHit(@Param("id") int id);
}