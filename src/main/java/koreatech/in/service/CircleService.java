package koreatech.in.service;

import koreatech.in.domain.Circle.Circle;
import koreatech.in.domain.Criteria.Criteria;

import java.util.Map;

public interface CircleService {
    Map<String, Object> getCirclesForAdmin(Criteria criteria) throws Exception;

    Circle createCircleForAdmin(Circle circle) throws Exception;

    Map<String, Object> getCircles(Criteria criteria) throws Exception;

    Map<String, Object> getCircle(int page, int limit, int id) throws Exception;

    Circle getCircleForAdmin(int id) throws Exception;

    Circle updateCircleForAdmin(Circle circle, int id) throws Exception;

    Map<String, Object> deleteCircleForAdmin(int id) throws Exception;
}
