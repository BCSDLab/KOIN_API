package koreatech.in.dto.member.admin.request;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MembersCondition extends Criteria {
    @ApiParam(value = "정렬 기준 \n" +
                      "- NULL 가능 \n" +
                      "- 다음 중 하나로만 요청 가능\n" +
                      "   - NAME_ASC: 이름 오름차순 \n" +
                      "   - NAME_DESC: 이름 내림차순 \n" +
                      "   - CREATED_AT_ASC: 등록순 \n" +
                      "   - CREATED_AT_DESC: 최신순(등록순의 반대) \n")
    private Sort sort;

    @ApiParam(value = "트랙 \n" +
                      "- NULL 가능 \n" +
                      "- 다음 중 하나로만 요청 가능 \n" +
                      "   - ANDROID \n" +
                      "   - BACKEND \n" +
                      "   - FRONTEND \n" +
                      "   - GAME \n" +
                      "   - UI_UX \n")
    private Track track;

    @ApiParam(value = "직책 \n" +
                      "- NULL 가능 \n" +
                      "- 다음 중 하나로만 요청 가능 \n" +
                      "   - MENTOR \n" +
                      "   - REGULAR")
    private Position position;

    @ApiParam(value = "필터링 기준 (리스트)\n" +
                      "- NULL 가능 \n" +
                      "- 다음 중 선택하여 요청 가능 \n" +
                      "   - IS_DELETED")
    private List<Filter> filter = new ArrayList<>();

    @ApiParam(value = "검색 대상 \n" +
                      "- NULL 가능 \n" +
                      "- 기본값: NAME \n" +
                      "- 다음중 하나로만 요청 가능 \n" +
                      "   - NAME (이름 검색)")
    private SearchType searchType = SearchType.NAME;

    @ApiParam(value = "검색 문자열 \n" +
                      "- NULL 가능 \n" +
                      "- NULL이 아닐 경우에는 문자열이 공백 문자로만 구성되어 있으면 안됨")
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

    private enum Filter {
        IS_DELETED
    }

    private enum SearchType {
        NAME
    }
}
