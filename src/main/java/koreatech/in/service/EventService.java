package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Event.EventComment;

import java.util.Map;

public interface EventService {

    Map<String, Object> getEventArticles(Criteria criteria) throws Exception;

    EventArticle createEventArticle(EventArticle eventArticle) throws Exception;

    EventArticle updateEventArticle(EventArticle eventArticle, int id) throws Exception;

    Map<String, Object> getEventArticle(int id) throws Exception;

    Map<String, Object> deleteEventArticle(int id) throws Exception;

    EventComment createEventComment(EventComment comment, int articleId) throws Exception;

//    EventComment getEventComment(int articleId, int commentId) throws Exception;

    EventComment updateEventComment(EventComment comment, int articleId, int commentId) throws Exception;

    Map<String, Object> deleteEventComment(int articleId, int commentId) throws Exception;

    Map<String, Object> getMyShops();

    Map<String, Object> getMyPendingEvent(Criteria criteria) throws Exception;

    Map<String, Object> getPendingEvents(Criteria criteria) throws Exception;

    Map<String, Object> getClosedEvents(Criteria criteria) throws Exception;

    Map<String, Boolean> checkGrantEditEvent(int articleId) throws Exception;

    EventArticle getRandomPendingEvent();
}
