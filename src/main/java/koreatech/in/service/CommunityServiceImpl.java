package koreatech.in.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import koreatech.in.domain.Community.*;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.User.User;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.ForbiddenException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.CommunityMapper;
import koreatech.in.util.DateUtil;
import koreatech.in.util.SearchUtil;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.StringRedisUtilObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;

import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;

@Service("communityService")
public class CommunityServiceImpl implements CommunityService {

    private static Map<Integer, String> board_url = new HashMap<Integer, String>() {{
        put(1, "free");
        put(2, "job");
        put(3, "anonymous");
        put(10, "question");
    }};

    @Resource(name = "communityMapper")
    private CommunityMapper communityMapper;

    @Autowired
    private SearchUtil searchUtil;

    @Autowired
    JwtValidator jwtValidator;

    @Autowired
    SlackNotiSender slackNotiSender;

    @Autowired
    private StringRedisUtilObj stringRedisUtilObj;

    @Override
    public Board createBoardForAdmin(Board board) throws Exception {
        if (communityMapper.getBoardByTagForAdmin(board.getTag()) != null) {
            throw new ConflictException(new ErrorMessage("board already exist", 2));
        }

        if (board.getIs_notice() == null) {
            board.setIs_notice(false);
        }
        if (board.getIs_anonymous() == null) {
            board.setIs_anonymous(false);
        }
        if (board.getSeq() == null) {
            board.setSeq(0);
        }
        if (board.getIs_deleted() == null) {
            board.setIs_deleted(false);
        }

        communityMapper.createBoardForAdmin(board);

        return board;
    }

    @Override
    public Map<String, Object> getBoardsForAdmin(Criteria criteria) {
        double totalCount = communityMapper.totalBoardCountForAdmin();
        double countByLimit = totalCount / criteria.getLimit();
        double totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        List<Board> boards = communityMapper.getBoardListForAdmin(criteria.getCursor(), criteria.getLimit());

        Map<String, Object> map = new HashMap<String, Object>() {{
            put("boards", boards);
            put("totalPage", totalPage);
            put("totalItemCount", totalCount);
        }};

        return map;
    }

    @Override
    public Board getBoardForAdmin(int id) throws Exception {
        return communityMapper.getBoardForAdmin(id);
    }

    @Override
    public Board updateBoardForAdmin(Board board, int id) throws Exception {
        Board board_old = communityMapper.getBoardForAdmin(id);

        if (board.getTag() != null) {
            Board selectBoard = communityMapper.getBoardByTagForAdmin(board.getTag());
            if (selectBoard != null && !selectBoard.getId().equals(id)) {
                throw new ConflictException(new ErrorMessage("duplicate board tag", 0));
            }
        }

        board_old.update(board);
        communityMapper.updateBoard(board_old);

        return board_old;
    }

    @Override
    public Map<String, Object> deleteBoardForAdmin(int id) throws Exception {
        communityMapper.deleteBoardForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public Article createArticleForAdmin(Article article) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        Board board = communityMapper.getBoardForAdmin(article.getBoard_id());

        if (board == null)
            throw new NotFoundException(new ErrorMessage("invalid board id", 2));

        article.setIs_notice(board.getIs_notice());

        article.setUser_id(user.getId());
        //TODO: (B)기존 Purifier로 content 필터링했던 부분 추가
        article.setNickname(user.getNickname());

        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();

        article.setIp(ip);

        if (article.getIs_solved() == null) {
            article.setIs_solved(false);
        }
        if (article.getIs_deleted() == null) {
            article.setIs_deleted(false);
        }

        communityMapper.createArticleForAdmin(article);

        if (!article.getIs_deleted()) {
            board.setArticle_count(board.getArticle_count() + 1);
            communityMapper.updateBoard(board);
        }

        searchUtil.createArticle(article);

        return article;
    }

    @Override
    public Map<String, Object> getArticlesForAdmin(int boardId, Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Board board = communityMapper.getBoardForAdmin(boardId);
        if (board == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        List<Article> articles;
        int totalPage;

        if (board.getIs_notice() && board.getTag().equals("NA000")) {
            double totalCount = communityMapper.totalNoticeArticleCountForAdmin();
            double countByLimit = totalCount / criteria.getLimit();
            totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
            if (totalPage < 0)
                throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

            articles = communityMapper.getNoticeArticlesForAdmin(criteria.getCursor(), criteria.getLimit());
        } else {
            double totalCount = communityMapper.totalArticleCountForAdmin(boardId);
            double countByLimit = totalCount / criteria.getLimit();
            totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
            if (totalPage < 0)
                throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

            articles = communityMapper.getArticleListForAdmin(boardId, criteria.getCursor(), criteria.getLimit());
        }

        map.put("articles", articles);
        map.put("totalPage", totalPage);
        map.put("board", board);

        return map;
    }

    @Override
    public Map<String, Object> getArticleForAdmin(int id) throws Exception {
        Article article = communityMapper.getArticleForAdmin(id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        List<Comment> comments = communityMapper.getCommentListForAdmin(id);

        for (Comment comment : comments) {
            comment.setGrantEdit(true);
            comment.setGrantDelete(true);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        //블랙 리스트 or null 컬럼 없애기
        //TODO: example 방법, 모든 response type을 도메인으로 정의하고 map으로 변환시 나머지 제거
        Map<String, Object> map = domainToMapWithExcept(article, ArticleResponseType.getAdminArray());
        if (article.getIs_notice()) {
            try {
                //meta 컬럼을 json_decode 해서 공지사항 올라온 날짜로 created_at 입력
                Map<String, Object> json = gson.fromJson(article.getMeta(), type);
                map.replace("created_at", json.get("registered_at"));
                map.put("permalink", json.get("permalink"));
            } catch (Exception e) {
            }
        }

        map.put("board", communityMapper.getBoardForAdmin(article.getBoard_id()));
        map.put("comments", comments);

        return map;
    }

    @Transactional
    @Override
    public Article updateArticleForAdmin(Article article, int id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        Article article_old = communityMapper.getArticleForAdmin(id);
        if (article_old == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        article_old.update(article);
        communityMapper.updateArticle(article_old);
        searchUtil.updateArticle(article_old);

        return article_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteArticleForAdmin(int id) throws Exception {
        Article article = communityMapper.getArticleForAdmin(id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        communityMapper.deleteArticleForAdmin(id);
        searchUtil.updateArticle(article);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public Comment createCommentForAdmin(Comment comment, int article_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("nickname is required", 0));

        Article article = communityMapper.getArticleForAdmin(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        Board board = communityMapper.getBoardForAdmin(article.getBoard_id());
        if (board == null)
            throw new NotFoundException(new ErrorMessage("There is no board", 0));

        comment.setArticle_id(article_id);
        comment.setUser_id(user.getId());
        comment.setNickname(user.getNickname());
        if (comment.getIs_deleted() == null) {
            comment.setIs_deleted(false);
        }

        communityMapper.createComment(comment);
        if (!comment.getIs_deleted()) {
            article.setComment_count(article.getComment_count() + 1);
            communityMapper.updateArticle(article);
        }

        return comment;
    }

    @Override
    public Comment getCommentForAdmin(int article_id, int comment_id) throws Exception {
        Article article = communityMapper.getArticleForAdmin(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        return communityMapper.getCommentForAdmin(article_id, comment_id);
    }

    @Transactional
    @Override
    public Comment updateCommentForAdmin(Comment comment, int article_id, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("nickname is required", 0));

        Article article = communityMapper.getArticleForAdmin(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        Comment comment_old = communityMapper.getCommentForAdmin(article_id, comment_id);

        //빈 객체인지 체크
        if (comment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        comment_old.update(comment);
        communityMapper.updateComment(comment_old);

        return comment_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteCommentForAdmin(int articleId, int comment_id) throws Exception {
        Article article = communityMapper.getArticle(articleId);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        Comment comment = communityMapper.getComment(articleId, comment_id);
        if (comment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        communityMapper.deleteCommentForAdmin(comment_id);
        article.setComment_count(article.getComment_count() - 1);
        communityMapper.updateArticle(article);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public List<Board> getBoards() {
        List<Board> parentBoards = communityMapper.getParentBoardList();

        for (Board board : parentBoards) {
            board.setChildren(communityMapper.getChildrenBoardList(board.getId()));
        }

        return parentBoards;
    }

    @Override
    public Board getBoard(int id) throws Exception {
        Board board = communityMapper.getBoard(id);
        if (board == null)
            throw new NotFoundException(new ErrorMessage("There is no board", 0));

        return board;
    }

    @Override
    public Map<String, Object> getArticles(int boardId, Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Board board = communityMapper.getBoard(boardId);
        if (board == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        List<Article> articles;
        int totalPage;

        if (board.getIs_notice() && board.getTag().equals("NA000")) {
            double totalCount = communityMapper.totalNoticeArticleCount();
            double countByLimit = totalCount / criteria.getLimit();
            totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
            if (totalPage < 0)
                throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

            articles = communityMapper.getNoticeArticles(criteria.getCursor(), criteria.getLimit());
        } else {
            double totalCount = communityMapper.totalArticleCount(boardId);
            double countByLimit = totalCount / criteria.getLimit();
            totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
            if (totalPage < 0)
                throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

            articles = communityMapper.getArticleList(boardId, criteria.getCursor(), criteria.getLimit());
        }

        map.put("articles", articles);
        map.put("totalPage", totalPage);
        map.put("board", board);

        return map;
    }

    @Override
    public List<Article> getNewArticles(int boardId, Criteria criteria) throws Exception {
        Board board = communityMapper.getBoard(boardId);
        if (board == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (board.getIs_notice() && board.getTag().equals("NA000")) {
            List<Article> articles = communityMapper.getNoticeArticles(criteria.getCursor(), criteria.getLimit());
            if (articles.size() > 0) articles.get(0).getContentSummary();

            return articles;
        }

        List<Article> articles = communityMapper.getArticleList(boardId, criteria.getCursor(), criteria.getLimit());
        if (articles.size() > 0) articles.get(0).getContentSummary();

        return articles;
    }

    @Override
    public List<Map<String, Object>> getCachedHotArticle() throws Exception {
        String cacheKey = Article.getHotArticlesCacheKey();

        List<Map<String, Object>> ret = new ArrayList<>();
        ret = (List<Map<String, Object>>) stringRedisUtilObj.getDataAsString(cacheKey, ret.getClass());
        return ret;
    }

    @Transactional
    @Override
    public Article createArticle(Article article) throws Exception {
        User user = jwtValidator.validate();
        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("닉네임이 필요합니다.", 2));

        Board board = communityMapper.getBoard(article.getBoard_id());

        if (board == null)
            throw new NotFoundException(new ErrorMessage("invalid board id", 2));

        article.setIs_notice(false);
        article.setUser_id(user.getId());
        //TODO: (B)기존 Purifier로 content 필터링했던 부분 추가
        article.setNickname(user.getNickname());

        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();

        article.setIp(ip);

        communityMapper.createArticle(article);
        searchUtil.createArticle(article);

        board.setArticle_count(board.getArticle_count() + 1);
        communityMapper.updateBoard(board);

        slackNotiSender.noticePost(NotiSlack.builder()
                .color("#36a64f")
                .author_name(board.getName() + " : " + user.getNickname() + "님이 작성")
                .title(article.getTitle())
                .title_link("https://koreatech.in" + "/board/" + board_url.get(board.getId()) + "/" + article.getId().toString())
                .text(article.getContentSummary())
                .build());

        return article;
    }

    @Override
    public Map<String, Object> getArticle(int id) throws Exception {
        User user = jwtValidator.validate();

        Article article = communityMapper.getArticle(id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (user != null) {
            // Get ip address
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = req.getHeader("X-FORWARDED-FOR");
            if (ip == null)
                ip = req.getRemoteAddr();

            if (shouldIncreaseArticleHit(article, user, ip)) {
                communityMapper.increaseHit(article.getId());
            }
        }
        List<Comment> comments = communityMapper.getCommentList(id);

        for (Comment comment : comments) {
            if (user != null && (user.getId().equals(comment.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_community()))) {
                comment.setGrantEdit(true);
                comment.setGrantDelete(true);
            } else {
                comment.setGrantEdit(false);
                comment.setGrantDelete(false);
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        //블랙 리스트 or null 컬럼 없애기
        //TODO: example 방법, 모든 response type을 도메인으로 정의하고 map으로 변환시 나머지 제거
        Map<String, Object> map = domainToMapWithExcept(article, ArticleResponseType.getArray());
        if (article.getIs_notice()) {
            try {
                //meta 컬럼을 json_decode 해서 공지사항 올라온 날짜로 created_at 입력
                Map<String, Object> json = gson.fromJson(article.getMeta(), type);
                map.replace("created_at", json.get("registered_at"));
                map.put("permalink", json.get("permalink"));
            } catch (Exception e) {
            }
        }

        map.put("board", communityMapper.getBoard(article.getBoard_id()));
        map.put("comments", comments);

        return map;
    }

    @Transactional
    @Override
    public Article updateArticle(Article article, int id) throws Exception {
        User user = jwtValidator.validate();
        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("닉네임이 필요합니다.", 2));

        Article article_old = communityMapper.getArticle(id);
        if (article_old == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (!article_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        //TODO : validator를 사용해 입력된 정보의 유효화 검사 후 입력된 부분만 기존 내용에 반영
        article.setNickname(user.getNickname());

        article_old.update(article);
        communityMapper.updateArticle(article_old);
        searchUtil.updateArticle(article_old);

        return article_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteArticle(int id) throws Exception {
        User user = jwtValidator.validate();

        Article article = communityMapper.getArticle(id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (!article.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        article.setIs_deleted(true);

        communityMapper.updateArticle(article);
        searchUtil.updateArticle(article);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public Comment createComment(Comment comment, int article_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("닉네임이 필요합니다.", 0));

        Article article = communityMapper.getArticle(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        Board board = communityMapper.getBoard(article.getBoard_id());
        if (board == null)
            throw new NotFoundException(new ErrorMessage("There is no board", 0));

        comment.setArticle_id(article_id);
        comment.setUser_id(user.getId());
        comment.setNickname(user.getNickname());

        communityMapper.createComment(comment);
        article.setComment_count(article.getComment_count() + 1);
        communityMapper.updateArticle(article);

        slackNotiSender.noticeComment(NotiSlack.builder()
                .color("#36a64f")
                .author_name(board.getName() + " : " + user.getNickname() + "님이 작성")
                .title(article.getTitle())
                .title_link("https://koreatech.in" + "/board/" + board_url.get(board.getId()) + "/" + article.getId().toString())
                .text(comment.getContent() + "...")
                .build());

        return comment;
    }

    @Override
    public Comment getComment(int article_id, int comment_id) throws Exception {
        Article article = communityMapper.getArticle(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        return communityMapper.getComment(article_id, comment_id);
    }

    @Transactional
    @Override
    public Comment updateComment(Comment comment, int article_id, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("nickname is required", 0));

        //게시글이 삭제되었는지 체크
        Article article = communityMapper.getArticle(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        Comment comment_old = communityMapper.getComment(article_id, comment_id);

        //빈 객체인지 체크
        if (comment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        if (!comment_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        comment.setNickname(user.getNickname());

        comment_old.update(comment);
        communityMapper.updateComment(comment_old);

        return comment_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteComment(int articleId, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        Article article = communityMapper.getArticle(articleId);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        Comment comment = communityMapper.getComment(articleId, comment_id);
        if (comment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        if (!comment.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        comment.setIs_deleted(true);

        communityMapper.updateComment(comment);
        article.setComment_count(article.getComment_count() - 1);
        communityMapper.updateArticle(article);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Boolean> checkGrantEditArticle(int article_id) throws Exception {
        User user = jwtValidator.validate();

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        Article article = communityMapper.getArticle(article_id);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (user.getId().equals(article.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_community())) {
            map.put("grantEdit", true);
        } else {
            map.put("grantEdit", false);
        }
        return map;
    }

    public Boolean shouldIncreaseArticleHit(Article article, User user, String ip) {
        try {
            ArticleViewLog viewLog = communityMapper.getViewLog(article.getId(), user.getId());
            if (viewLog != null && (viewLog.getExpired_at().getTime() - (new Date()).getTime() > 0)) return false;

            Date expiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);

            //TODO: update Or insert 구현시 개선
            if (viewLog == null) {
                viewLog = new ArticleViewLog();
                viewLog.setArticle_id(article.getId());
                viewLog.setUser_id(user.getId());
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                communityMapper.createViewLog(viewLog);
            } else {
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                communityMapper.updateViewLog(viewLog);
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
