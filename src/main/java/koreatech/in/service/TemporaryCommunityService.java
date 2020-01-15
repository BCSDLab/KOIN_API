package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.TemporaryCommunity.TempArticle;
import koreatech.in.domain.TemporaryCommunity.TempComment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface TemporaryCommunityService {

    Map<String, Object> getArticles(Criteria criteria) throws Exception;

    TempArticle createArticle(TempArticle article) throws Exception;

    Map<String, Object> getArticle(int id) throws Exception;

    TempArticle updateArticle(TempArticle article, int id) throws Exception;

    Map<String, Object> deleteArticle(int id, String password) throws Exception;

    TempComment createComment(TempComment comment, int article_id) throws Exception;

    Map<String, Object> getComment(int article_id, int comment_id) throws Exception;

    TempComment updateComment(TempComment comment, int article_id, int comment_id) throws Exception;

    Map<String, Object> deleteComment(int articleId, int comment_id, String password) throws Exception;

    Map<String, Boolean> checkGrantEditArticle(int article_id, String password) throws Exception;

    Map<String, Boolean> checkGrantEditComment(int comment_id, String password) throws Exception;

    Map<String, Object> itemImagesUpload(Map<String, MultipartFile> fileMap) throws Exception;

    Map<String, Object> itemThumbnailImageUpload(MultipartFile image) throws Exception;
}
