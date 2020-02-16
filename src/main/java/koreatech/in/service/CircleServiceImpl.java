package koreatech.in.service;

import koreatech.in.domain.BeanSerializer;
import koreatech.in.domain.Circle.Circle;
import koreatech.in.domain.Circle.CircleResponseType;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Faq.Faq;
import koreatech.in.domain.Faq.FaqResponseType;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.CircleMapper;
import koreatech.in.repository.FaqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CircleServiceImpl implements CircleService {
    @Inject
    CircleMapper circleMapper;

    @Inject
    FaqMapper faqMapper;

    @Autowired
    private JsonConstructor con;

    @Override
    public Map<String, Object> getCirclesForAdmin(Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        double totalCount = circleMapper.totalCount();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        List<Circle> circleList = circleMapper.getCircleListForAdmin(criteria.getCursor(), criteria.getLimit());

        map.put("circles", circleList);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", totalCount);

        return map;
    }

    @Override
    public Circle createCircleForAdmin(Circle circle) throws Exception {
        if (!Circle.isValidCategory(circle.getCategory())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid category name", 1));
        }

        Circle selectCircle = circleMapper.getCircleByName(circle.getName());
        if (selectCircle != null) {
            throw new ConflictException(new ErrorMessage("exists circle name", 0));
        }

        //link_urls 체크
        if (circle.getLink_urls() != null && !con.isJsonArrayWithOnlyObject(circle.getLink_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        if (circle.getIs_deleted() == null) {
            circle.setIs_deleted(false);
        }

        circleMapper.createCircleForAdmin(circle);

        return circle;
    }

    @Override
    public Map<String, Object> getCircles(Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        double totalCount = circleMapper.totalCount();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        List<Circle> circles = circleMapper.getCircleList(criteria.getCursor(), criteria.getLimit());

        map.put("circles", circles);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", totalCount);

        return map;
    }

    @Override
    public Map<String, Object> getCircle(int page, int limit, int id) throws Exception {
        Circle circle = circleMapper.getCircle(id);
        if (circle == null) {
            throw new NotFoundException(new ErrorMessage("not found", 0));
        }

        Map<String, Object> map = (Map<String, Object>) BeanSerializer.getSerializedResult(Circle.class, new BeanSerializer(CircleResponseType.getArray()), circle, Map.class);

        if (limit > 50) {
            limit = 50;
        }

        int faqTotalCount = faqMapper.totalCountByCircle(id);
        int faqTotalPage = (int) Math.ceil(faqTotalCount / (double) limit);

        int cursor = (page - 1) * limit;

        Map<String, Object> faq = new HashMap<String, Object>();
        faq.put("items", BeanSerializer.getSerializedResult(Faq.class, new BeanSerializer(FaqResponseType.getArray()), faqMapper.getFaqListByCircle(cursor, limit, id), List.class));
        faq.put("totalPage", faqTotalPage);
        faq.put("totalItemCount", faqTotalCount);

        map.replace("link_urls", con.parseJsonArrayWithObject(circle.getLink_urls()));
        map.put("faq", faq);

        return map;
    }

    @Override
    public Circle getCircleForAdmin(int id) throws Exception {
        Circle circle = circleMapper.getCircleForAdmin(id);

        if (circle == null) {
            throw new NotFoundException(new ErrorMessage("not found", 0));
        }

        return circle;
    }

    @Override
    public Circle updateCircleForAdmin(Circle circle, int id) throws Exception {
        Circle selectCircle = circleMapper.getCircleForAdmin(id);
        if (selectCircle == null) {
            throw new NotFoundException(new ErrorMessage("No Circle", 0));
        }

        Circle selectCircle2 = circleMapper.getCircleByName(circle.getName());
        if (selectCircle2 != null && !selectCircle2.getId().equals(id)) {
            throw new ConflictException(new ErrorMessage("exists circle name", 0));
        }

        if (circle.getCategory() != null && !Circle.isValidCategory(circle.getCategory())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid category name", 1));
        }

        //link_urls 체크
        if (circle.getLink_urls() != null && !con.isJsonArrayWithOnlyObject(circle.getLink_urls()))
            throw new PreconditionFailedException(new ErrorMessage("link_urls are not valid", 0));

        selectCircle.update(circle);
        circleMapper.updateCircleForAdmin(selectCircle);

        return selectCircle;
    }

    @Override
    public Map<String, Object> deleteCircleForAdmin(int id) throws Exception {
        Circle circle = circleMapper.getCircleForAdmin(id);

        if (circle == null) {
            throw new NotFoundException(new ErrorMessage("No Circle", 0));
        }

        circleMapper.deleteCircleForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }
}
