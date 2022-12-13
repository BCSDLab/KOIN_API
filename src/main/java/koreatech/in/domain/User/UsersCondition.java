package koreatech.in.domain.User;

import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsersCondition extends Criteria {
    private SearchType searchType = SearchType.ALL;
    private String query;

    @Getter
    private enum SearchType {
        ALL(0),
        PORTAL_ACCOUNT(1),
        NICKNAME(2),
        NAME(3);

        SearchType(int value) {
            this.value = value;
        }

        private final int value;
    }
}
