package koreatech.in.dto.admin.shop.request;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.exception.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import static koreatech.in.exception.ExceptionInformation.*;

@Getter @Setter
public class ShopCategoriesCondition extends Criteria {
    @ApiParam("정렬 기준 \n" +
              "- null 가능 \n" +
              "- 다음중 하나로만 요청 가능 \n" +
              "  - NAME_ASC: 이름 오름차순 \n" +
              "  - NAME_DESC: 이름 내림차순 \n" +
              "  - CREATED_AT_ASC: 등록순 \n" +
              "  - CREATED_AT_DESC: 최신순(등록순의 반대)")
    private Sort sort;

    @ApiParam("검색 대상 \n" +
              "- null일 경우 기본값: NAME \n" +
              "- 다음중 하나로만 요청 가능 \n" +
              "  - NAME (이름 검색)")
    private SearchType searchType = SearchType.NAME;

    @ApiParam("검색 문자열 \n" +
              "- null이 아닐 경우에는 다음의 조건을 만족해야 함 \n" +
              "  - 길이는 1 이상 \n" +
              "  - 공백 문자로만 이루어져 있으면 안됨")
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
