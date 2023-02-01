package koreatech.in.dto.admin.shop.request;

import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ShopCategoriesCondition extends Criteria {
    private Sort sort;
    private SearchType searchType = SearchType.NAME;
    private String query;

    private enum Sort {
        NAME_ASC,
        NAME_DESC,
        CREATED_AT_ASC,
        CREATED_AT_DESC
    }

    private enum SearchType {
        NAME
    }
}
