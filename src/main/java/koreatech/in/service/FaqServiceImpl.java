package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Faq.Faq;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.FaqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FaqServiceImpl implements FaqService {
    @Autowired
    FaqMapper faqMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Override
    public Faq createFaqForAdmin(Faq faq) throws Exception {
        if (faq.getCircle_id() == null) {
            faq.setCircle_id(0);
        }
        if (faq.getIs_deleted() == null) {
            faq.setIs_deleted(false);
        }
        faqMapper.createFaqForAdmin(faq);

        return faq;
    }

    @Override
    public Faq updateFaqForAdmin(Faq faq, int id) throws Exception {
        Faq faq_old = faqMapper.getFaqForAdmin(id);
        if (faq_old == null) {
            throw new NotFoundException(new ErrorMessage("No FAQ", 0));
        }

        faq_old.update(faq);
        faqMapper.updateFaqForAdmin(faq_old);

        return faq_old;
    }

    @Override
    public Map<String, Object> deleteFaqForAdmin(int id) throws Exception {
        Faq faq = faqMapper.getFaqForAdmin(id);
        if (faq == null) {
            throw new NotFoundException(new ErrorMessage("No FAQ", 0));
        }

        faqMapper.deleteFaqForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Object> getFaqList(Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<>();

        Integer totalPage = criteria.calcTotalPage(faqMapper.totalCount());

        map.put("faqs", faqMapper.getFaqList(criteria.getCursor(), criteria.getLimit()));
        map.put("totalPage", totalPage);

        return map;
    }

    @Override
    public Faq getFaq(int id) throws Exception {
        Faq faq = faqMapper.getFaq(id);

        if(faq == null) {
            throw new NotFoundException(new ErrorMessage("there is no item", 0));
        }

        return faq;
    }


}
