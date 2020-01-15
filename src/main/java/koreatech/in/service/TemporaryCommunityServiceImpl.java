package koreatech.in.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.TemporaryCommunity.TempArticle;
import koreatech.in.domain.TemporaryCommunity.TempComment;
import koreatech.in.domain.TemporaryCommunity.TempArticleResponseType;
import koreatech.in.domain.TemporaryCommunity.TempCommentResponseType;
import koreatech.in.exception.*;
import koreatech.in.repository.TemporaryCommunityMapper;
import koreatech.in.util.SearchUtil;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;

@Service("tempCommunityService")
public class TemporaryCommunityServiceImpl implements TemporaryCommunityService {
    @Resource(name="temporaryCommunityMapper")
    private TemporaryCommunityMapper temporaryCommunityMapper;

    @Autowired
    SlackNotiSender slackNotiSender;

    @Autowired
    private SearchUtil searchUtil;

    @Inject
    UploadFileUtils uploadFileUtils;

    @Override
    public Map<String, Object> getArticles(Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        double totalCount = temporaryCommunityMapper.totalArticleCount();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int)Math.ceil(totalCount / criteria.getLimit());
        if (totalPage<0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        map.put("articles",temporaryCommunityMapper.getArticleList(criteria.getCursor(), criteria.getLimit()));
        map.put("totalPage",totalPage);

        return map;

    }

    @Transactional
    @Override
    public TempArticle createArticle(TempArticle article) throws Exception {
        article.setIs_deleted(false);

        temporaryCommunityMapper.createArticle(article);

        searchUtil.createArticle(article);

        NotiSlack slack_message = new NotiSlack();
        String name = article.getNickname();
        slack_message.setColor("#36a64f");
        slack_message.setAuthor_name("익명게시판 : "+ name + "님이 작성");
        slack_message.setTitle(article.getTitle());
        slack_message.setTitle_link("https://koreatech.in/board/anonymous/" + article.getId().toString());
        slack_message.setText(article.getContentSummary());

        slackNotiSender.noticePost(slack_message);

        return article;

    }

    @Override
    public Map<String, Object> getArticle(int id) throws Exception {
        TempArticle article = temporaryCommunityMapper.getArticle(id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        temporaryCommunityMapper.increaseHit(article.getId());

        List<TempComment> comments = temporaryCommunityMapper.getCommentList(id);

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String,Object>>(){}.getType();
        //블랙 리스트 or null 컬럼 없애기
        //TODO: example 방법, 모든 response type을 도메인으로 정의하고 map으로 변환시 나머지 제거
        Map<String, Object> map = domainToMapWithExcept(article, TempArticleResponseType.getArray());

        map.put("comments", comments);

        return map;
    }

    @Transactional
    @Override
    public TempArticle updateArticle(TempArticle article, int id) throws Exception {
        TempArticle article_old = temporaryCommunityMapper.getArticle(id);
        if (article_old == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if(!article_old.hasGrantUpdate(article.getPassword()))
            throw new ForbiddenException(new ErrorMessage("Password incorrect", 0));

        //TODO : validator를 사용해 입력된 정보의 유효화 검사 후 입력된 부분만 기존 내용에 반영

        article_old.update(article);
        temporaryCommunityMapper.updateArticle(article_old);
        return article_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteArticle(int id, String password) throws Exception {
        TempArticle article = temporaryCommunityMapper.getArticle(id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if(!article.hasGrantDelete(password))
            throw new ForbiddenException(new ErrorMessage("Password incorrect", 0));

        article.setIs_deleted(true);

        temporaryCommunityMapper.updateArticle(article);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public TempComment createComment(TempComment comment, int article_id) throws Exception {
        TempArticle article = temporaryCommunityMapper.getArticle(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        comment.setArticle_id(article_id);
        comment.setIs_deleted(false);

        temporaryCommunityMapper.createComment(comment);
        article.setComment_count(article.getComment_count() + 1);
        temporaryCommunityMapper.updateArticle(article);

        NotiSlack slack_message = new NotiSlack();
        String name = comment.getNickname();

        slack_message.setColor("#36a64f");
        slack_message.setAuthor_name("익명게시판 : " + name + "님이 작성");
        slack_message.setTitle(article.getTitle());
        slack_message.setTitle_link("https://koreatech.in/board/anonymous/" + article.getId().toString());
        slack_message.setText(comment.getContent() + "...");

        slackNotiSender.noticeComment(slack_message);

        return comment;
    }

    @Override
    public Map<String, Object> getComment(int article_id, int comment_id) throws Exception {
        TempArticle article = temporaryCommunityMapper.getArticle(article_id);
        if (article == null || article.getIs_deleted())
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        TempComment comment = temporaryCommunityMapper.getComment(article_id, comment_id);
        if (comment == null || comment.getIs_deleted())
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        //TODO: example 방법, 모든 response type을 도메인으로 정의하고 map으로 변환시 나머지 제거
        Map<String, Object> map_comment = domainToMapWithExcept(comment, TempCommentResponseType.getArray());
        return map_comment;
    }

    @Transactional
    @Override
    public TempComment updateComment(TempComment comment, int article_id, int comment_id) throws Exception {
        //게시글이 삭제되었는지 체크
        TempArticle article = temporaryCommunityMapper.getArticle(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        TempComment comment_old = temporaryCommunityMapper.getComment(article_id, comment_id);

        //빈 객체인지 체크
        if (comment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        if(!comment_old.hasGrantUpdate(comment.getPassword()))
            throw new ForbiddenException(new ErrorMessage("Password incorrect", 0));

        comment_old.update(comment);
        temporaryCommunityMapper.updateComment(comment_old);

        return comment_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteComment(int articleId, int comment_id, String password) throws Exception {
        TempArticle article = temporaryCommunityMapper.getArticle(articleId);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 2));

        TempComment comment = temporaryCommunityMapper.getComment(articleId, comment_id);
        if (comment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        if(!comment.hasGrantDelete(password))
            throw new ForbiddenException(new ErrorMessage("Password incorrect", 0));

        comment.setIs_deleted(true);

        temporaryCommunityMapper.updateComment(comment);
        article.setComment_count(article.getComment_count() - 1);
        temporaryCommunityMapper.updateArticle(article);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Boolean> checkGrantEditArticle(int article_id, String password) throws Exception {
        Map<String, Boolean> map = new HashMap<String, Boolean>();

        TempArticle article = temporaryCommunityMapper.getArticle(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (article.hasGrantUpdate(password)) {
            map.put("grantEdit", true);
        } else {
            map.put("grantEdit", false);
        }
        return map;
    }

    @Override
    public Map<String, Boolean> checkGrantEditComment(int comment_id, String password) throws Exception {
        Map<String, Boolean> map = new HashMap<String, Boolean>();

        TempComment comment = temporaryCommunityMapper.getCommentById(comment_id);
        if (comment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        if (comment.hasGrantUpdate(password)) {
            map.put("grantEdit", true);
        } else {
            map.put("grantEdit", false);
        }
        return map;
    }

    @Override
    public Map<String, Object> itemImagesUpload(Map<String, MultipartFile> fileMap) throws Exception {
        String uploadpath = "upload/temp";

        List<String> urls = new ArrayList<>();

        for (MultipartFile mf : fileMap.values()) {
            String img_path = uploadFileUtils.uploadFile(uploadpath, mf.getOriginalFilename(), mf.getBytes());
            String url = "https://" + uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

            urls.add(url);
        }

        return new HashMap<String, Object>(){{
            put("url", urls);
        }};
    }

    @Override
    public Map<String, Object> itemThumbnailImageUpload(MultipartFile image) throws Exception {
        String uploadpath = "upload/temp/thumbnail";

        String originalFileName = image.getOriginalFilename();
        int index = originalFileName.lastIndexOf(".");
        String fileName = originalFileName.substring(0, index);
        String fileExt = originalFileName.substring(index+1);

        File file = new File(fileName);
        image.transferTo(file);

        uploadFileUtils.makeThumbnail(file.getAbsolutePath(), originalFileName, fileExt, 500, 500);


        String img_path = uploadFileUtils.uploadFile(uploadpath, originalFileName, image.getBytes());
        String url = "https://" + uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

        // TODO: 스케줄 처리할 것
        uploadFileUtils.removeThumbnail(file.getAbsolutePath(), originalFileName, fileExt);

        return new HashMap<String, Object>(){{
            put("url", url);
        }};
    }
}
