package koreatech.in.repository;

import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.LostAndFound.LostItemComment;
import koreatech.in.domain.LostAndFound.LostItemViewLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("lostAndFoundMapper")
public interface LostAndFoundMapper {
    @Insert("INSERT INTO koin.lost_items (TYPE, TITLE, CONTENT, USER_ID, NICKNAME, STATE, PHONE, IS_PHONE_OPEN, THUMBNAIL, IP, LOCATION, DATE, IMAGE_URLS) " +
            "VALUES (#{type}, #{title}, #{content}, #{user_id}, #{nickname}, #{state}, #{phone}, #{is_phone_open}, #{thumbnail}, #{ip}, #{location}, #{date}, #{image_urls})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createLostItem(LostItem lostItem);

    @Insert("INSERT INTO koin.lost_items (TYPE, TITLE, CONTENT, USER_ID, NICKNAME, STATE, PHONE, IS_PHONE_OPEN, THUMBNAIL, IP, LOCATION, DATE, IMAGE_URLS, IS_DELETED) " +
            "VALUES (#{type}, #{title}, #{content}, #{user_id}, #{nickname}, #{state}, #{phone}, #{is_phone_open}, #{thumbnail}, #{ip}, #{location}, #{date}, #{image_urls}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createLostItemForAdmin(LostItem lostItem);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.lost_items WHERE TYPE = #{type} AND IS_DELETED = 0")
    Integer totalItemCountByType(@Param("type") int type);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.lost_items WHERE TYPE = #{type}")
    Integer totalItemCountByTypeForAdmin(@Param("type") int type);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.lost_items WHERE IS_DELETED = 0")
    Integer totalItemCount();

    @Select("SELECT COUNT(*) AS totalCount FROM koin.lost_items")
    Integer totalItemCountForAdmin();

    @Select("SELECT * FROM koin.lost_items WHERE IS_DELETED = 0 AND TYPE = #{type} ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<LostItem> getLostItemListByType(@Param("type") int type, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.lost_items WHERE TYPE = #{type} ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<LostItem> getLostItemListByTypeForAdmin(@Param("type") int type, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.lost_items WHERE IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<LostItem> getLostItemList(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.lost_items ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<LostItem> getLostItemListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.lost_items WHERE ID = #{id} AND IS_DELETED = 0")
    LostItem getLostItem(@Param("id") int id);

    @Select("SELECT * FROM koin.lost_items WHERE ID = #{id}")
    LostItem getLostItemForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.lost_item_comments WHERE LOST_ITEM_ID = #{lost_item_id} AND IS_DELETED = 0")
    List<LostItemComment> getCommentList(@Param("lost_item_id") int id);

    @Select("SELECT * FROM koin.lost_item_comments WHERE LOST_ITEM_ID = #{lost_item_id}")
    List<LostItemComment> getCommentListForAdmin(@Param("lost_item_id") int id);

    @Update("UPDATE koin.lost_items SET TYPE=#{type}, TITLE=#{title}, LOCATION=#{location}, CONTENT=#{content}, NICKNAME=#{nickname}, IP=#{ip}, STATE=#{state}, PHONE=#{phone}, " +
            "IS_PHONE_OPEN=#{is_phone_open}, IMAGE_URLS=#{image_urls}, THUMBNAIL=#{thumbnail}, DATE=#{date}, " +
            "IS_DELETED=#{is_deleted}, COMMENT_COUNT=#{comment_count} WHERE ID = #{id}")
    void updateLostItem(LostItem lostItem);

    @Select("SELECT * FROM koin.lost_items WHERE TYPE = #{type} AND USER_ID = #{user_id} AND IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<LostItem> getMyLostItemList(@Param("cursor") int cursor, @Param("limit") int limit, @Param("type") int type, @Param("user_id") int user_id);

    @Insert("INSERT INTO koin.lost_item_comments (LOST_ITEM_ID, CONTENT, USER_ID, NICKNAME)" +
            "VALUES (#{lost_item_id}, #{content}, #{user_id}, #{nickname})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createLostItemComment(LostItemComment lostItemComment);

    @Insert("INSERT INTO koin.lost_item_comments (LOST_ITEM_ID, CONTENT, USER_ID, NICKNAME, IS_DELETED)" +
            "VALUES (#{lost_item_id}, #{content}, #{user_id}, #{nickname}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createLostItemCommentForAdmin(LostItemComment lostItemComment);

    @Select("SELECT * FROM koin.lost_item_comments WHERE ID = #{comment_id} AND LOST_ITEM_ID = #{lost_item_id} AND IS_DELETED = 0")
    LostItemComment getLostItemComment(@Param("lost_item_id") int lost_item_id, @Param("comment_id") int comment_id);

    @Select("SELECT * FROM koin.lost_item_comments WHERE ID = #{comment_id} AND LOST_ITEM_ID = #{lost_item_id}")
    LostItemComment getLostItemCommentForAdmin(@Param("lost_item_id") int lost_item_id, @Param("comment_id") int comment_id);

    @Update("UPDATE koin.lost_item_comments SET LOST_ITEM_ID=#{lost_item_id}, CONTENT=#{content}, USER_ID=#{user_id}, NICKNAME=#{nickname}, IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateLostItemComment(LostItemComment lostItemComment);

    @Delete("DELETE FROM koin.lost_items WHERE ID = #{id}")
    void deleteLostItem(@Param("id") int id);

    @Delete("DELETE FROM koin.lost_item_comments WHERE ID = #{id}")
    void deleteLostItemComment(@Param("id") int id);

    @Select("SELECT * FROM koin.lost_item_view_logs WHERE LOST_ITEM_ID = #{lost_item_id} AND USER_ID = #{user_id} ORDER BY ID DESC LIMIT 1")
    LostItemViewLog getViewLog(@Param("lost_item_id") int lost_item_id, @Param("user_id") int user_id);

    @Insert("INSERT INTO koin.lost_item_view_logs (LOST_ITEM_ID, USER_ID, EXPIRED_AT, IP)" +
            "VALUES (#{lost_item_id}, #{user_id}, #{expired_at}, #{ip})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createViewLog(LostItemViewLog viewLog);

    @Update("UPDATE koin.lost_item_view_logs SET LOST_ITEM_ID=#{lost_item_id}, USER_ID=#{user_id}, EXPIRED_AT=#{expired_at}, IP=#{ip} WHERE ID = #{id}")
    void updateViewLog(LostItemViewLog viewLog);

    @Update("UPDATE koin.lost_items SET HIT = HIT + 1 WHERE IS_DELETED = 0 AND ID = #{id}")
    void increaseHit(@Param("id") int id);
}
