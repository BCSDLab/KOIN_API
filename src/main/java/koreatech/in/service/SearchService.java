package koreatech.in.service;

import koreatech.in.domain.Criteria.SearchCriteria;

import java.util.Map;

public interface SearchService {
    Map<String, Object> searchShop(SearchCriteria searchCriteria) throws Exception;

    Map<String, Object> searchCommunity(SearchCriteria searchCriteria) throws Exception;
}
