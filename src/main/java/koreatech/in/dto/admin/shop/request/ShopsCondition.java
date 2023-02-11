package koreatech.in.dto.admin.shop.request;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.exception.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static koreatech.in.exception.ExceptionInformation.*;

@Getter @Setter
public class ShopsCondition extends Criteria {
    @ApiParam("정렬 기준 \n" +
            "- null 가능 \n" +
            "- 다음중 하나로만 요청 가능 \n" +
            "  - NAME_ASC: 이름 오름차순 \n" +
            "  - NAME_DESC: 이름 내림차순 \n" +
            "  - CREATED_AT_ASC: 등록순 \n" +
            "  - CREATED_AT_DESC: 최신순(등록순의 반대)")
    private Sort sort;

    @ApiParam("필터링 기준 \n" +
              "- 다음중 1개 이상을 배열로 요청 가능 \n" +
              "  - IS_DELETED (삭제 여부) \n" +
              "  - DELIVERY (배달 가능) \n" +
              "  - PAY_CARD (카드 계산 가능 여부) \n" +
              "  - PAY_BANK (계좌 이체 가능 여부)")
    private List<Filter> filter = new ArrayList<>();

    @ApiParam("검색 대상 \n" +
            "- null일 경우 기본값: NAME \n" +
            "- 다음중 하나로만 요청 가능 \n" +
            "  - NAME (이름 검색)")
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
