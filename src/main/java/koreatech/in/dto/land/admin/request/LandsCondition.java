package koreatech.in.dto.land.admin.request;

import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LandsCondition extends Criteria {
    private SearchType searchType = SearchType.NAME;
    private String query;

    @Getter
    private enum SearchType {
        NAME(1);

        SearchType(int value) {
            this.value = value;
        }

        private final int value;
    }
}
