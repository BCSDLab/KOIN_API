package koreatech.in.service;

import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Community.Board;
import koreatech.in.domain.Community.Comment;
import koreatech.in.domain.Criteria.Criteria;

import java.util.List;
import java.util.Map;

public interface CommunityService {

    Board createBoardForAdmin(Board board) throws Exception;

    Map<String, Object> getBoardsForAdmin(Criteria criteria) throws Exception;

    Board getBoardForAdmin(int id) throws Exception;

    Board updateBoardForAdmin(Board board, int id) throws Exception;

    Map<String, Object> deleteBoardForAdmin(int id) throws Exception;

    Article createArticleForAdmin(Article article) throws Exception;

    Map<String, Object> getArticlesForAdmin(int boardId, Criteria criteria) throws Exception;

    Map<String, Object> getArticleForAdmin(int id) throws Exception;

    Article updateArticleForAdmin(Article article, int id) throws Exception;

    Map<String, Object> deleteArticleForAdmin(int id) throws Exception;

    Comment createCommentForAdmin(Comment comment, int article_id) throws Exception;

    Comment getCommentForAdmin(int article_id, int comment_id) throws Exception;

    Comment updateCommentForAdmin(Comment comment, int article_id, int comment_id) throws Exception;

    Map<String, Object> deleteCommentForAdmin(int articleId, int comment_id) throws Exception;

    List<Board> getBoards() throws Exception;

    Board getBoard(int id) throws Exception;

    Map<String, Object> getArticles(int boardId, Criteria criteria) throws Exception;

    List<Article> getNewArticles(int boardId, Criteria criteria) throws Exception;

    List<Map<String, Object>> getCachedHotArticle() throws Exception;

    Article createArticle(Article article) throws Exception;

    Map<String, Object> getArticle(int id) throws Exception;

    Article updateArticle(Article article, int id) throws Exception;

    Map<String, Object> deleteArticle(int id) throws Exception;

    Comment createComment(Comment comment, int article_id) throws Exception;

    Comment getComment(int article_id, int comment_id) throws Exception;

    Comment updateComment(Comment comment, int article_id, int comment_id) throws Exception;

    Map<String, Object> deleteComment(int articleId, int comment_id) throws Exception;

    Map<String, Boolean> checkGrantEditArticle(int article_id) throws Exception;

}
