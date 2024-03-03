package koreatech.in.dto.admin.member.request;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.exception.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import static koreatech.in.exception.ExceptionInformation.SEARCH_QUERY_LENGTH_AT_LEAST_1;
import static koreatech.in.exception.ExceptionInformation.SEARCH_QUERY_MUST_NOT_BE_BLANK;

@Getter @Setter
public class MembersCondition extends Criteria {
    @ApiParam(value = "정렬 기준 \n" +
                      "- 다음 중 하나로만 요청 가능\n" +
                      "   - NAME_ASC: 이름 오름차순 \n" +
                      "   - NAME_DESC: 이름 내림차순 \n" +
                      "   - CREATED_AT_ASC: 등록순 \n" +
                      "   - CREATED_AT_DESC: 최신순(등록순의 반대) \n")
    private Sort sort;

    @ApiParam(value = "트랙 \n" +
                      "- 다음 중 하나로만 요청 가능 \n" +
                      "   - ANDROID \n" +
                      "   - BACKEND \n" +
                      "   - FRONTEND \n" +
                      "   - GAME \n" +
                      "   - UI_UX \n")
    private Track track;

    @ApiParam(value = "직책 \n" +
                      "- 다음 중 하나로만 요청 가능 \n" +
                      "   - MENTOR \n" +
                      "   - REGULAR")
    private Position position;

    @ApiParam(value = "삭제(soft delete) 여부")
    private Boolean is_deleted;

    @ApiParam(value = "검색 대상 \n" +
                      "- null일 경우 기본값: NAME \n" +
                      "- 다음중 하나로만 요청 가능 \n" +
                      "   - NAME (이름 검색)")
    private SearchType searchType = SearchType.NAME;

    @ApiParam(value = "검색 문자열 \n" +
                      "- null이 아닐 경우에는 다음의 조건들을 만족해야함 \n" +
                      "  - 길이는 1 이상 \n" +
                      "  - 공백문자로만 이루어져 있으면 안됨")
    private String query;

    private enum Sort {
        NAME_ASC, // 이름 오름차순
        NAME_DESC, // 이름 내림차순
        CREATED_AT_ASC, // 등록순
        CREATED_AT_DESC // 최신순
    }

    private enum Track {
        ANDROID,
        BACKEND,
        FRONTEND,
        GAME,
        HR,
        UI_UX
    }

    private enum Position {
        MENTOR,
        REGULAR
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
