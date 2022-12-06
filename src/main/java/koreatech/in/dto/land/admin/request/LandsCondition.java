package koreatech.in.dto.land.admin.request;

import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class LandsCondition extends Criteria {
    private Sort sort;
    private List<Filter> filter = new ArrayList<>();
    private SearchType searchType = SearchType.NAME;
    private String query;

    private enum Sort {
        NAME_ASC, // 이름 오름차순
        NAME_DESC, // 이름 내림차순
        CREATED_AT_ASC, // 등록순
        CREATED_AT_DESC // 최신순
    }

    private enum Filter {
        IS_DELETED, // 삭제 여부
        REFRIGERATOR, // 냉장고
        CLOSET, // 옷장
        TV, // TV
        MICROWAVE, // 전자레인지
        GAS_RANGE, // 가스레인지
        INDUCTION, // 인덕션
        WATER_PURIFIER, // 정수기
        AIR_CONDITIONER, // 에어
        WASHER, // 세탁기
        BED, // 침대
        DESK, // 책상
        SHOE_CLOSET, // 신발장
        ELECTRONIC_DOOR_LOCKS, // 도어락
        BIDET, // 비데
        VERANDA, // 베란다
        ELEVATOR, // 엘리베이터
    }

    private enum SearchType {
        NAME
    }
}
