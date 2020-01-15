package koreatech.in.repository;

import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Community.ArticleViewLog;
import koreatech.in.domain.Community.Board;
import koreatech.in.domain.Community.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("communityMapper")
public interface CommunityMapper {
    @Select("SELECT * FROM koin.boards WHERE TAG = #{tag}")
    Board getBoardByTagForAdmin(@Param("tag") String tag);

    @Insert("INSERT INTO koin.boards (tag, name, is_anonymous, is_deleted, is_notice, seq, parent_id) " +
            "VALUES (#{tag}, #{name}, #{is_anonymous}, #{is_deleted}, #{is_notice}, #{seq}, #{parent_id})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createBoardForAdmin(Board board);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.boards")
    Integer totalBoardCountForAdmin();

    @Select("SELECT * FROM koin.boards ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Board> getBoardListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Delete("DELETE FROM koin.boards WHERE ID = #{id}")
    void deleteBoardForAdmin(@Param("id") int id);

    @Delete("DELETE FROM koin.articles WHERE ID = #{id}")
    void deleteArticleForAdmin(@Param("id") int id);

    @Delete("DELETE FROM koin.comments WHERE ID = #{id}")
    void deleteCommentForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.boards WHERE parent_id is NULL AND IS_DELETED = 0 ORDER BY seq")
    List<Board> getParentBoardList();

    @Select("SELECT * FROM koin.boards WHERE PARENT_ID = #{id} AND IS_DELETED = 0")
    List<Board> getChildrenBoardList(@Param("id") int id);

    @Select("SELECT * FROM koin.boards WHERE PARENT_ID = #{id}")
    List<Board> getChildrenBoardListForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.boards WHERE ID = #{id} AND IS_DELETED = 0")
    Board getBoard(@Param("id") int id);

    @Select("SELECT * FROM koin.boards WHERE ID = #{id}")
    Board getBoardForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.articles WHERE IS_DELETED = 0 AND BOARD_ID = #{boardId} ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Article> getArticleList(@Param("boardId") int boardId, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.articles WHERE BOARD_ID = #{boardId} ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Article> getArticleListForAdmin(@Param("boardId") int boardId, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.articles WHERE BOARD_ID = #{boardId} AND IS_DELETED = 0")
    Integer totalArticleCount(@Param("boardId") int boardId);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.articles WHERE BOARD_ID = #{boardId}")
    Integer totalArticleCountForAdmin(@Param("boardId") int boardId);

    @Select("SELECT * FROM koin.articles WHERE IS_NOTICE = 1 AND IS_DELETED = 0 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Article> getNoticeArticles(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT * FROM koin.articles WHERE IS_NOTICE = 1 ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Article> getNoticeArticlesForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.articles WHERE IS_NOTICE = 1 AND IS_DELETED = 0")
    Integer totalNoticeArticleCount();

    @Select("SELECT COUNT(*) AS totalCount FROM koin.articles WHERE IS_NOTICE = 1")
    Integer totalNoticeArticleCountForAdmin();

    @Insert("INSERT INTO koin.articles (BOARD_ID, TITLE, CONTENT, USER_ID, NICKNAME, IP, IS_SOLVED, META, IS_NOTICE, NOTICE_ARTICLE_ID) " +
            "VALUES (#{board_id}, #{title}, #{content}, #{user_id}, #{nickname}, #{ip}, #{is_solved}, #{meta}, #{is_notice}, #{notice_article_id})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createArticle(Article article);
    // hit, is_deleted, created_at, updated_at 컬럼 제외

    @Insert("INSERT INTO koin.articles (BOARD_ID, TITLE, CONTENT, USER_ID, NICKNAME, IP, IS_SOLVED, META, IS_NOTICE, NOTICE_ARTICLE_ID, iS_DELETED) " +
            "VALUES (#{board_id}, #{title}, #{content}, #{user_id}, #{nickname}, #{ip}, #{is_solved}, #{meta}, #{is_notice}, #{notice_article_id}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createArticleForAdmin(Article article);

    @Update("UPDATE koin.boards SET TAG=#{tag}, NAME=#{name}, IS_ANONYMOUS=#{is_anonymous}, ARTICLE_COUNT=#{article_count}, IS_DELETED=#{is_deleted}, IS_NOTICE=#{is_notice}, PARENT_ID=#{parent_id}, SEQ=#{seq} " +
            "WHERE ID = #{id}")
    void updateBoard(Board board);

    @Select("SELECT * FROM koin.articles WHERE ID = #{id} AND IS_DELETED = 0")
    Article getArticle(@Param("id") int id);

    @Select("SELECT * FROM koin.articles WHERE ID = #{id}")
    Article getArticleForAdmin(@Param("id") int id);

    @Update("UPDATE koin.articles SET BOARD_ID=#{board_id}, TITLE=#{title}, CONTENT=#{content}, USER_ID=#{user_id}, NICKNAME=#{nickname}, IP=#{ip}, IS_SOLVED=#{is_solved}, COMMENT_COUNT=#{comment_count}, META=#{meta}, IS_NOTICE=#{is_notice}, NOTICE_ARTICLE_ID=#{notice_article_id}, " +
            "IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateArticle(Article article);

    @Select("SELECT * FROM koin.comments WHERE ARTICLE_ID = #{article_id} AND IS_DELETED = 0")
    List<Comment> getCommentList(@Param("article_id") int id);

    @Select("SELECT * FROM koin.comments WHERE ARTICLE_ID = #{article_id}")
    List<Comment> getCommentListForAdmin(@Param("article_id") int id);

    @Insert("INSERT INTO koin.comments (ARTICLE_ID, CONTENT, USER_ID, NICKNAME)" +
            "VALUES (#{article_id}, #{content}, #{user_id}, #{nickname})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createComment(Comment comment);

    @Insert("INSERT INTO koin.comments (ARTICLE_ID, CONTENT, USER_ID, NICKNAME, IS_DELETED)" +
            "VALUES (#{article_id}, #{content}, #{user_id}, #{nickname}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createCommentForAdmin(Comment comment);

    @Select("SELECT * FROM koin.comments WHERE ID = #{comment_id} AND ARTICLE_ID = #{article_id} AND IS_DELETED = 0")
    Comment getComment(@Param("article_id") int article_id, @Param("comment_id") int comment_id);

    @Select("SELECT * FROM koin.comments WHERE ID = #{comment_id} AND ARTICLE_ID = #{article_id}")
    Comment getCommentForAdmin(@Param("article_id") int article_id, @Param("comment_id") int comment_id);

    @Update("UPDATE koin.comments SET ARTICLE_ID=#{article_id}, CONTENT=#{content}, USER_ID=#{user_id}, NICKNAME=#{nickname}, IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateComment(Comment comment);

    @Select("SELECT * FROM koin.article_view_logs WHERE ARTICLE_ID = #{article_id} AND USER_ID = #{user_id} ORDER BY ID DESC LIMIT 1")
    ArticleViewLog getViewLog(@Param("article_id") int article_id, @Param("user_id") int user_id);

    @Insert("INSERT INTO koin.article_view_logs (ARTICLE_ID, USER_ID, EXPIRED_AT, IP)" +
            "VALUES (#{article_id}, #{user_id}, #{expired_at}, #{ip})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createViewLog(ArticleViewLog viewLog);

    @Update("UPDATE koin.article_view_logs SET ARTICLE_ID=#{article_id}, USER_ID=#{user_id}, EXPIRED_AT=#{expired_at}, IP=#{ip} WHERE ID = #{id}")
    void updateViewLog(ArticleViewLog viewLog);

    @Select("SELECT * FROM (select * FROM articles where is_deleted = 0 order by created_at DESC LIMIT 100) CNT order by hit DESC LIMIT 10")
    List<Article> getHotArticles();

    @Update("UPDATE koin.articles SET HIT = HIT + 1 WHERE IS_DELETED = 0 AND ID = #{id}")
    void increaseHit(@Param("id") int id);

}