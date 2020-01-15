package koreatech.in.repository;

import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Search.SearchArticles;
import koreatech.in.domain.Search.SearchComments;
import koreatech.in.domain.Shop.Menu;
import koreatech.in.domain.Shop.Shop;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("searchMapper")
public interface SearchMapper {
    @Select("SELECT * FROM koin.shops WHERE is_deleted = 0 AND name LIKE CONCAT('%', #{name}, '%') ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Shop> findShopByName(@Param("name") String name, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM koin.shops WHERE is_deleted = 0 AND name LIKE CONCAT('%', #{name}, '%')")
    Integer totalShopCountByName(@Param("name") String name);

    @Select("SELECT * FROM koin.shop_menus WHERE is_deleted = 0 AND name LIKE CONCAT('%', #{name}, '%') ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Menu> findMenuByName(@Param("name") String name, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM koin.shop_menus WHERE is_deleted = 0 AND name LIKE CONCAT('%', #{name}, '%')")
    Integer totalMenuCountByName(@Param("name") String name);

    @Select("SELECT * FROM koin.search_articles WHERE is_deleted = 0 AND title LIKE CONCAT('%', #{title}, '%') ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<SearchArticles> findArticlesByTitle(@Param("title") String title, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM koin.search_articles WHERE is_deleted = 0 AND title LIKE CONCAT('%', #{title}, '%')")
    Integer totalArticlesCountByTitle(@Param("title") String title);

    @Select("SELECT * FROM koin.search_articles WHERE is_deleted = 0 AND (title LIKE CONCAT('%', #{query}, '%') OR content LIKE CONCAT('%', #{query}, '%')) ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<SearchArticles> findArticlesByTitleAndContent(@Param("query") String query, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM koin.search_articles WHERE is_deleted = 0 AND (title LIKE CONCAT('%', #{query}, '%') OR content LIKE CONCAT('%', #{query}, '%'))")
    Integer totalArticlesCountByTitleAndContent(@Param("query") String query);

    @Select("SELECT * FROM koin.search_articles WHERE is_deleted = 0 AND nickname LIKE CONCAT('%', #{nickname}, '%') ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<SearchArticles> findArticlesByNickname(@Param("nickname") String nickname, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM koin.search_articles WHERE is_deleted = 0 AND nickname LIKE CONCAT('%', #{nickname}, '%')")
    Integer totalArticlesCountByNickname(@Param("nickname") String nickname);

    @Select("SELECT * FROM koin.search_comments WHERE is_deleted = 0 AND content LIKE CONCAT('%', #{content}, '%') ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<SearchComments> findCommentsByContent(@Param("content") String content, @Param("cursor") int cursor, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM koin.search_comments WHERE is_deleted = 0 AND content LIKE CONCAT('%', #{content}, '%')")
    Integer totalCommentsCountByContent(@Param("content") String content);

    @Insert("INSERT INTO koin.search_articles (table_id, article_id, title, content, user_id, nickname, is_deleted) " +
            "VALUES (#{table_id}, #{article_id}, #{title}, #{content}, #{user_id}, #{nickname}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createSearchArticles(SearchArticles searchArticles);

    @Update("UPDATE koin.search_articles SET TITLE=#{title}, CONTENT=#{content}, NICKNAME=#{nickname}, IS_DELETED=#{is_deleted} WHERE ID=#{id}")
    void updateSearchArticles(SearchArticles searchArticles);

    @Select("SELECT * FROM koin.search_articles WHERE table_id=#{table_id} AND is_deleted = 0 AND article_id=#{article_id}")
    SearchArticles findArticlesByTableIdAndArticleId(@Param("table_id") Integer table_id, @Param("article_id") Integer article_id);

    @Select("SELECT * FROM koin.search_articles WHERE table_id=#{table_id} AND article_id=#{article_id}")
    SearchArticles findArticlesByTableIdAndArticleIdForAdmin(@Param("table_id") Integer table_id, @Param("article_id") Integer article_id);

    @Select("SELECT * FROM koin.search_comments WHERE table_id=#{table_id} AND is_deleted = 0 AND comment_id=#{comment_id}")
    SearchComments findCommentsByTableIdAndCommentId(@Param("table_id") Integer table_id, @Param("comment_id") Integer comment_id);

    @Select("SELECT * FROM koin.search_comments WHERE table_id=#{table_id} AND comment_id=#{comment_id}")
    SearchComments findCommentsByTableIdAndACommentIdForAdmin(@Param("table_id") Integer table_id, @Param("comment_id") Integer comment_id);
}
