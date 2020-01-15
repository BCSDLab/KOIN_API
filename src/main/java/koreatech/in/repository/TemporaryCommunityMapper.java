package koreatech.in.repository;

import koreatech.in.domain.TemporaryCommunity.TempArticle;
import koreatech.in.domain.TemporaryCommunity.TempComment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("temporaryCommunityMapper")
public interface TemporaryCommunityMapper {
    @Select("SELECT id, title, content, nickname, hit, comment_count, created_at, updated_at FROM koin.temp_articles WHERE IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<TempArticle> getArticleList(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.temp_articles WHERE IS_DELETED = 0")
    Integer totalArticleCount();

    @Insert("INSERT INTO koin.temp_articles (TITLE, CONTENT, NICKNAME, PASSWORD) " +
            "VALUES (#{title}, #{content}, #{nickname}, #{password})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createArticle(TempArticle article);
    // hit, is_deleted, created_at, updated_at 컬럼 제외

    @Select("SELECT * FROM koin.temp_articles WHERE ID = #{id} AND IS_DELETED = 0")
    TempArticle getArticle(@Param("id") int id);

    @Update("UPDATE koin.temp_articles SET TITLE=#{title}, CONTENT=#{content}, NICKNAME=#{nickname}, PASSWORD=#{password}, COMMENT_COUNT=#{comment_count}, " +
            "IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateArticle(TempArticle article);

    @Select("SELECT id, article_id, content, nickname, created_at, updated_at FROM koin.temp_comments WHERE ARTICLE_ID = #{article_id} AND IS_DELETED = 0")
    List<TempComment> getCommentList(@Param("article_id") int id);

    @Insert("INSERT INTO koin.temp_comments (ARTICLE_ID, CONTENT, NICKNAME, PASSWORD, IS_DELETED)" +
            "VALUES (#{article_id}, #{content}, #{nickname}, #{password}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createComment(TempComment comment);

    @Select("SELECT * FROM koin.temp_comments WHERE ID = #{comment_id} AND ARTICLE_ID = #{article_id} AND IS_DELETED = 0")
    TempComment getComment(@Param("article_id") int article_id, @Param("comment_id") int comment_id);

    @Select("SELECT * FROM koin.temp_comments WHERE ID = #{comment_id} AND IS_DELETED = 0")
    TempComment getCommentById(@Param("comment_id") int comment_id);

    @Update("UPDATE koin.temp_comments SET ARTICLE_ID=#{article_id}, CONTENT=#{content}, NICKNAME=#{nickname}, PASSWORD=#{password}, IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateComment(TempComment comment);

    @Update("UPDATE koin.temp_articles SET HIT = HIT + 1 WHERE IS_DELETED = 0 AND ID = #{id}")
    void increaseHit(@Param("id") int id);
}
