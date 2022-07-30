package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Event.EventArticleViewLog;
import koreatech.in.domain.Event.EventComment;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.User.User;
import koreatech.in.exception.ForbiddenException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.EventMapper;
import koreatech.in.util.DateUtil;
import koreatech.in.util.SearchUtil;
import koreatech.in.util.SlackNotiSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private SlackNotiSender slackNotiSender;

    @Autowired
    private SearchUtil searchUtil;

    @Override
    public Map<String, Object> getEventArticles(Criteria criteria) throws Exception {
        double totalCount = eventMapper.totalEventArticleCount();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 페이지입니다.", 2));

        List<EventArticle> eventArticles = eventMapper.getEventArticleList(criteria.getCursor(), criteria.getLimit());
        Map<String, Object> map = new HashMap<>();
        map.put("event_articles", eventArticles);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Transactional
    @Override
    public EventArticle createEventArticle(EventArticle eventArticle) throws Exception {
        User user = jwtValidator.validate();
        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("닉네임이 필요합니다.", 2));

        if (eventMapper.totalMyPendingEventCount(user.getId()) > 0)
            throw new PreconditionFailedException(new ErrorMessage("이미 진행 중인 이벤트가 있습니다.", 0));

        eventArticle.setUser_id(user.getId());
        eventArticle.setNickname(user.getNickname());

        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();

        eventArticle.setIp(ip);

        // 이벤트 시작 - 종료 일자 검증
        LocalDate endDate = LocalDate.parse(eventArticle.getEnd_date()), startDate = LocalDate.parse(eventArticle.getStart_date());
        LocalDate nowDate = LocalDate.now();

        if (endDate.isBefore(nowDate))
            throw new PreconditionFailedException(new ErrorMessage("행사 마감일은 오늘보다 이전일 수 없습니다.", 0));

        if (endDate.isAfter(startDate.plusMonths(1)))
            throw new PreconditionFailedException(new ErrorMessage("행사 마감일은 시작일의 한 달 이후를 넘길 수 없습니다.", 0));
        // 검증 완료

        eventMapper.createEventArticle(eventArticle);
        searchUtil.createArticle(eventArticle);

//        slackNotiSender.noticeLostItem(NotiSlack.builder()
//                .color("#36a64f")
//                .author_name(eventArticle.getNickname() + "님이 작성")
//                .title(eventArticle.getTitle())
//                .title_link("https://koreatech.in/board/promotion/" + eventArticle.getId().toString())
//                .build());

        return eventArticle;
    }

    @Transactional
    @Override
    public EventArticle updateEventArticle(EventArticle eventArticle, int id) throws Exception {
        User user = jwtValidator.validate();

        EventArticle eventArticle_old = eventMapper.getEventArticle(id);
        if (eventArticle_old == null)
            throw new NotFoundException(new ErrorMessage("해당 게시글은 존재하지 않습니다.", 0));

        if (!eventArticle_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("수정할 수 있는 권한이 없습니다.", 0));

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("닉네임이 필요합니다.", 2));

        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        eventArticle.setIp(ip);

        eventArticle.setNickname(user.getNickname());

        eventArticle_old.update(eventArticle);

        // 이벤트 시작 - 종료 일자 검증
        LocalDate endDate = LocalDate.parse(eventArticle_old.getEnd_date()), startDate = LocalDate.parse(eventArticle_old.getStart_date());
        LocalDate nowDate = LocalDate.now();

        if (endDate.isBefore(nowDate))
            throw new PreconditionFailedException(new ErrorMessage("행사 마감일은 오늘보다 이전일 수 없습니다.", 0));

        if (endDate.isAfter(startDate.plusMonths(1)))
            throw new PreconditionFailedException(new ErrorMessage("행사 마감일은 시작일의 한 달 이후를 넘길 수 없습니다.", 0));
        // 검증 완료

        eventMapper.updateEventArticle(eventArticle_old);
        searchUtil.updateArticle(eventArticle_old);

        return eventArticle_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteEventArticle(int id) throws Exception {
        User user = jwtValidator.validate();

        EventArticle eventArticle = eventMapper.getEventArticle(id);
        if (eventArticle == null)
            throw new NotFoundException(new ErrorMessage("해당 게시글은 존재하지 않습니다.", 0));

        if (!eventArticle.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("삭제할 수 있는 권한이 없습니다.", 0));

        eventArticle.setIs_deleted(true);

        eventMapper.updateEventArticle(eventArticle);
        searchUtil.updateArticle(eventArticle);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Object> getEventArticle(int id) throws Exception {
        User user = jwtValidator.validate();

        EventArticle eventArticle = eventMapper.getEventArticle(id);
        if (eventArticle == null)
            throw new NotFoundException(new ErrorMessage("해당 게시글은 존재하지 않습니다.", 0));

        if (user != null) {
            // Get ip address
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = req.getHeader("X-FORWARDED-FOR");
            if (ip == null)
                ip = req.getRemoteAddr();

            if (shouldIncreaseEventArticleHit(eventArticle, user, ip)) {
                eventMapper.increaseHit(eventArticle.getId());
            }
        }
        List<EventComment> comments = eventMapper.getEventCommentList(id);

        for (EventComment comment : comments) {
            if (user != null && (user.getId().equals(comment.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_lost()))) {
                comment.setGrantEdit(true);
                comment.setGrantDelete(true);
            } else {
                comment.setGrantEdit(false);
                comment.setGrantDelete(false);
            }
        }

        Map<String, Object> map = domainToMap(eventArticle);
        map.put("comments", comments);

        return map;
    }

    @Transactional
    @Override
    public EventComment createEventComment(EventComment comment, int articleId) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("닉네임이 필요합니다.", 0));

        EventArticle article = eventMapper.getEventArticle(articleId);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("해당 게시글은 존재하지 않습니다.", 0));

        comment.setArticle_id(articleId);
        comment.setUser_id(user.getId());
        comment.setNickname(user.getNickname());

        eventMapper.createEventComment(comment);
        article.setComment_count(article.getComment_count() + 1);
        eventMapper.updateEventArticle(article);

//        slackNotiSender.noticeComment(NotiSlack.builder()
//                .color("#36a64f")
//                .author_name(user.getNickname() + "님이 작성")
//                .title(article.getTitle())
//                .title_link("https://koreatech.in/board/promotion/" + article.getId().toString())
//                .text(comment.getContent() + "...")
//                .build());

        return comment;
    }

//    @Override
//    public EventComment getEventComment(int articleId, int commentId) throws Exception {
//        EventArticle article = eventMapper.getEventArticle(articleId);
//        if (article == null)
//            throw new NotFoundException(new ErrorMessage("There is no article", 0));
//
//        EventComment comment = eventMapper.getEventComment(articleId, commentId);
//        if (comment == null)
//            throw new NotFoundException(new ErrorMessage("There is no comment", 0));
//
//        return comment;
//    }

    @Transactional
    @Override
    public EventComment updateEventComment(EventComment comment, int articleId, int commentId) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("닉네임이 필요합니다.", 0));

        //게시글이 삭제되었는지 체크
        EventArticle article = eventMapper.getEventArticle(articleId);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("해당 게시글은 존재하지 않습니다.", 0));

        EventComment comment_old = eventMapper.getEventComment(articleId, commentId);

        //빈 객체인지 체크
        if (comment_old == null)
            throw new NotFoundException(new ErrorMessage("해당 댓글은 존재하지 않습니다.", 0));

        if (!comment_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("수정할 수 있는 권한이 없습니다.", 0));

        comment.setNickname(user.getNickname());

        comment_old.update(comment);
        eventMapper.updateEventComment(comment_old);

        return comment_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteEventComment(int articleId, int commentId) throws Exception {
        User user = jwtValidator.validate();

        EventArticle article = eventMapper.getEventArticle(articleId);
        if (article == null)
            throw new NotFoundException(new ErrorMessage("해당 게시글은 존재하지 않습니다.", 0));

        EventComment comment = eventMapper.getEventComment(articleId, commentId);
        if (comment == null)
            throw new NotFoundException(new ErrorMessage("해당 댓글은 존재하지 않습니다.", 0));

        if (!comment.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("삭제할 수 있는 권한이 없습니다.", 0));

        comment.setIs_deleted(true);

        eventMapper.updateEventComment(comment);
        article.setComment_count(article.getComment_count() - 1);
        eventMapper.updateEventArticle(article);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    public Boolean shouldIncreaseEventArticleHit(EventArticle eventArticle, User user, String ip) {
        try {
            EventArticleViewLog viewLog = eventMapper.getViewLog(eventArticle.getId(), user.getId());
            if (viewLog != null && (viewLog.getExpired_at().getTime() - (new Date()).getTime() > 0)) return false;

            Date expiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);

            //TODO: update Or insert 구현시 개선
            if (viewLog == null) {
                viewLog = new EventArticleViewLog();
                viewLog.setEvent_articles_id(eventArticle.getId());
                viewLog.setUser_id(user.getId());
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                eventMapper.createViewLog(viewLog);
            } else {
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                eventMapper.updateViewLog(viewLog);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getMyShops() {
        User user = jwtValidator.validate();

        List<Map<String, Object>> myShops = eventMapper.getMyShops(user.getId());
        Map<String, Object> map = new HashMap<>();

        map.put("shops", myShops);
        return map;
    }

    @Override
    public Map<String, Object> getMyPendingEvent(Criteria criteria) throws Exception {
        User user = jwtValidator.validate();

        double totalCount = eventMapper.totalMyPendingEventCount(user.getId());
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 페이지입니다.", 2));

        List<EventArticle> myEvent = eventMapper.getMyPendingEvent(criteria.getCursor(), criteria.getLimit(), user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("event_articles", myEvent);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Override
    public Map<String, Object> getPendingEvents(Criteria criteria) throws Exception {
        double totalCount = eventMapper.totalPendingEventCount();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 페이지입니다.", 2));

        List<EventArticle> pendingEvents = eventMapper.getPendingEvents(criteria.getCursor(), criteria.getLimit());
        Map<String, Object> map = new HashMap<>();
        map.put("event_articles", pendingEvents);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);
        return map;
    }

    @Override
    public Map<String, Object> getClosedEvents(Criteria criteria) throws Exception {
        double totalCount = eventMapper.totalClosedEventCount();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 페이지입니다.", 2));

        List<EventArticle> closedEvents = eventMapper.getClosedEvents(criteria.getCursor(), criteria.getLimit());
        Map<String, Object> map = new HashMap<>();
        map.put("event_articles", closedEvents);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);
        return map;
    }

    @Override
    public EventArticle getRandomPendingEvent() {

        return eventMapper.getRandomPendingEvent();
    }

    @Override
    public Map<String, Boolean> checkGrantEditEvent(int articleId) throws Exception {
        User user = jwtValidator.validate();

        EventArticle eventArticle = eventMapper.getEventArticle(articleId);
        if (eventArticle == null)
            throw new NotFoundException(new ErrorMessage("해당 게시글은 존재하지 않습니다.", 0));

        Map<String, Boolean> map = new HashMap<>();
        if (user.getId().equals(eventArticle.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_event())) {
            map.put("grantEdit", true);
        } else {
            map.put("grantEdit", false);
        }
        return map;
    }
}
