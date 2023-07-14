package koreatech.in.dto.admin.user.request;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.exception.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import static koreatech.in.exception.ExceptionInformation.*;

@Getter @Setter
public class OwnersCondition extends Criteria {
    @ApiParam("검색 대상 \n" +
             "- query가 null이 아닐 경우 null이면 안됨 \n" +
             "- 다음중 하나로만 요청 가능 \n" +
             "  - `EMAIL` (이메일 검색) \n" +
             "  - `NAME` (이름 검색)")
    private SearchType search_type;

    @ApiParam("검색 문자열 \n" +
              "- null이 아닐 경우 다음의 조건을 만족해야 함 \n" +
              "  - 길이는 1 이상이어야 함 \n" +
              "  - 공백 문자로만 이루어져 있으면 안됨")
    private String query;

    @ApiParam("정렬 기준 \n" +
              "- null 가능 \n" +
              "- 다음중 하나로만 요청 가능 \n" +
              "  - `CREATED_AT_ASC` (오래된순) \n" +
              "  - `CREATED_AT_DESC` (최신순)")
    private Sort sort;

    @ApiParam("유저 권한 타입 \n"
            + "- null 가능, null일 경우 기본 값 ALL \n"
            + "- 다음 중 하나로만 요청 가능 \n"
            + " - `ALL` \n"
            + " - `AUTHED` \n"
            + " - `UNAUTHED`")
    private AuthType auth_type = AuthType.ALL;

    public enum SearchType {
        EMAIL,
        NAME
    }

    public enum Sort {
        CREATED_AT_ASC,
        CREATED_AT_DESC
    }

    private enum AuthType {
        ALL,
        AUTHED,
        UNAUTHED
    }

    public void checkDataConstraintViolation() {
        if (this.query != null) {
            checkSearchTypeNotNull();
            checkQueryIsEmpty();
            checkQueryIsBlank();
        }
    }

    private void checkSearchTypeNotNull() {
        if (this.search_type == null) {
            throw new BaseException(SEARCH_TYPE_SHOULD_NOT_BE_NULL_WHEN_QUERY_IS_NOT_NULL);
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
