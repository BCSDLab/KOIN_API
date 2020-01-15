package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Faq.Faq;

import java.util.Map;

public interface FaqService {
    Faq createFaqForAdmin(Faq faq) throws Exception;

    Faq updateFaqForAdmin(Faq faq, int id) throws Exception;

    Map<String, Object> deleteFaqForAdmin(int id) throws Exception;

    Map<String, Object> getFaqList(Criteria criteria) throws Exception;

    Faq getFaq(int id) throws Exception;
}
