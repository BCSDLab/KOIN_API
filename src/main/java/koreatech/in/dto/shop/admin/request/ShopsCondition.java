package koreatech.in.dto.shop.admin.request;

import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ShopsCondition extends Criteria {
    private Sort sort;
    private List<Filter> filter = new ArrayList<>();
    private SearchType searchType = SearchType.NAME;
    private String query;

    private enum Sort {
        NAME_ASC,
        NAME_DESC,
        CREATED_AT_ASC,
        CREATED_AT_DESC
    }

    private enum Filter {
        IS_DELETED,
        DELIVERY,
        PAY_CARD,
        PAY_BANK
    }

    private enum SearchType {
        NAME
    }
}
