package koreatech.in.repository;

import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Event.EventArticleViewLog;
import koreatech.in.domain.Event.EventComment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EventMapper {
    @Select("SELECT * FROM koin.event_articles WHERE ID = #{id} AND IS_DELETED = 0")
    EventArticle getEventArticle(@Param("id") int id);

    @Select("SELECT id, shop_id, title, event_title, content, user_id, nickname, thumbnail, hit, ip, start_date, end_date, comment_count, is_deleted, created_at, updated_at, " +
            "(" +
            "CASE " +
            "WHEN CURDATE() BETWEEN start_date AND end_date THEN 0 " +
            "WHEN (start_date > CURDATE()) THEN 1 " +
            "ELSE 2 END " +
            ") AS ord " +
            "FROM koin.event_articles WHERE IS_DELETED = 0 " +
            "ORDER BY " +
            "FIELD(ord, 0, 1, 2) ASC, " +
            "CASE WHEN ord = 0 THEN CREATED_AT END ASC, " +
            "CASE WHEN ord = 1 THEN CREATED_AT END ASC, " +
            "CASE WHEN ord = 2 THEN END_DATE END DESC " +
            "LIMIT #{cursor}, #{limit}")
    List<EventArticle> getEventArticleList(@Param("cursor") int cursor, @Param("limit") int limit);

    @Insert("INSERT INTO koin.event_articles (SHOP_ID, TITLE, EVENT_TITLE, CONTENT, USER_ID, NICKNAME, THUMBNAIL, IP, START_DATE, END_DATE) " +
            "VALUES (#{shop_id}, #{title}, #{event_title}, #{content}, #{user_id}, #{nickname}, #{thumbnail}, #{ip}, #{start_date}, #{end_date})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createEventArticle(EventArticle eventArticle);

    @Update("UPDATE koin.event_articles SET SHOP_ID=#{shop_id}, TITLE=#{title}, EVENT_TITLE=#{event_title}, CONTENT=#{content}, NICKNAME=#{nickname}, THUMBNAIL=#{thumbnail}, IP=#{ip}, " +
            "START_DATE=#{start_date}, END_DATE=#{end_date}, IS_DELETED=#{is_deleted}, COMMENT_COUNT=#{comment_count} WHERE ID = #{id}")
    void updateEventArticle(EventArticle eventArticle);

    @Insert("INSERT INTO koin.event_comments (ARTICLE_ID, CONTENT, USER_ID, NICKNAME)" +
            "VALUES (#{article_id}, #{content}, #{user_id}, #{nickname})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createEventComment(EventComment comment);

    @Select("SELECT * FROM koin.event_comments WHERE ID = #{comment_id} AND ARTICLE_ID = #{article_id} AND IS_DELETED = 0")
    EventComment getEventComment(@Param("article_id") int article_id, @Param("comment_id") int comment_id);

    @Select("SELECT * FROM koin.event_comments WHERE ARTICLE_ID = #{article_id} AND IS_DELETED = 0")
    List<EventComment> getEventCommentList(@Param("article_id") int id);

    @Update("UPDATE koin.event_comments SET ARTICLE_ID=#{article_id}, CONTENT=#{content}, USER_ID=#{user_id}, NICKNAME=#{nickname}, IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateEventComment(EventComment comment);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.event_articles WHERE IS_DELETED = 0")
    Integer totalEventArticleCount();

    @Select("SELECT * FROM koin.event_articles_view_log WHERE EVENT_ARTICLES_ID = #{event_articles_id} AND USER_ID = #{user_id} ORDER BY ID DESC LIMIT 1")
    EventArticleViewLog getViewLog(@Param("event_articles_id") int event_articles_id, @Param("user_id") int user_id);

    @Insert("INSERT INTO koin.event_articles_view_log (EVENT_ARTICLES_ID, USER_ID, EXPIRED_AT, IP)" +
            "VALUES (#{event_articles_id}, #{user_id}, #{expired_at}, #{ip})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createViewLog(EventArticleViewLog viewLog);

    @Update("UPDATE koin.event_articles_view_log SET EVENT_ARTICLES_ID=#{event_articles_id}, USER_ID=#{user_id}, EXPIRED_AT=#{expired_at}, IP=#{ip} WHERE ID = #{id}")
    void updateViewLog(EventArticleViewLog viewLog);

    @Update("UPDATE koin.event_articles SET HIT = HIT + 1 WHERE IS_DELETED = 0 AND ID = #{id}")
    void increaseHit(@Param("id") int id);

    @Select("SELECT shop.id AS shop_id, shop.name FROM koin.shops AS shop JOIN koin.shop_owners AS shop_owner ON shop_owner.shop_id = shop.id WHERE shop_owner.user_id = #{user_id} AND shop_owner.is_deleted = 0")
    List<Map<String, Object>> getMyShops(@Param("user_id") int user_id);

    @Select("SELECT * FROM koin.event_articles WHERE user_id = #{user_id} AND CURDATE() BETWEEN START_DATE AND END_DATE AND IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<EventArticle> getMyPendingEvent(@Param("cursor") int cursor, @Param("limit") int limit, @Param("user_id") int user_id);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.event_articles WHERE user_id = #{user_id} AND CURDATE() BETWEEN START_DATE AND END_DATE AND IS_DELETED = 0")
    Integer totalMyPendingEventCount(@Param("user_id") int user_id);

    @Select("SELECT * FROM koin.event_articles WHERE CURDATE() BETWEEN START_DATE AND END_DATE AND IS_DELETED = 0 ORDER BY created_at ASC LIMIT #{cursor}, #{limit}")
    List<EventArticle> getPendingEvents(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.event_articles WHERE CURDATE() BETWEEN START_DATE AND END_DATE AND IS_DELETED = 0")
    Integer totalPendingEventCount();

    @Select("SELECT * FROM koin.event_articles WHERE END_DATE < CURDATE() AND IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<EventArticle> getClosedEvents(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.event_articles WHERE END_DATE < CURDATE() AND IS_DELETED = 0")
    Integer totalClosedEventCount();

    @Select("SELECT * FROM koin.event_articles WHERE CURDATE() BETWEEN START_DATE AND END_DATE AND IS_DELETED = 0 ORDER BY RAND() LIMIT 1")
    EventArticle getRandomPendingEvent();
}
