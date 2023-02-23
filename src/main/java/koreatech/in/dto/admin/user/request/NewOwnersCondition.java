package koreatech.in.dto.admin.user.request;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.exception.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import static koreatech.in.exception.ExceptionInformation.SEARCH_QUERY_LENGTH_AT_LEAST_1;
import static koreatech.in.exception.ExceptionInformation.SEARCH_QUERY_MUST_NOT_BE_BLANK;

@Getter @Setter
public class NewOwnersCondition extends Criteria {
    private SearchType search_type;
    private String query;
    private Sort sort;

    private enum SearchType {
        EMAIL,
        NAME
    }

    private enum Sort {
        CREATED_AT_ASC,
        CREATED_AT_DESC
    }

    public void checkDataConstraintViolation() {
        if (this.query != null) {
            checkQueryIsEmpty();
            checkQueryIsBlank();
        }
    }

    private void checkQueryIsEmpty() {
        if (this.query.length() == 0) {
            throw new BaseException(SEARCH_QUERY_LENGTH_AT_LEAST_1);
        }
    }

    private void checkQueryIsBlank() {
        if (StringUtils.isBlank(this.query)) {
            throw new BaseException(SEARCH_QUERY_MUST_NOT_BE_BLANK);
        }
    }
}
