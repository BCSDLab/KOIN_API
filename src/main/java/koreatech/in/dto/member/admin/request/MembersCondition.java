package koreatech.in.dto.member.admin.request;

import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MembersCondition extends Criteria {
    private Sort sort;
    private Track track;
    private Position position;
    private List<Filter> filter = new ArrayList<>();
    private SearchType searchType = SearchType.NAME;
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
