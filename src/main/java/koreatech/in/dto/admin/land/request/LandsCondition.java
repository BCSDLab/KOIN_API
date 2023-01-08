package koreatech.in.dto.admin.land.request;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class LandsCondition extends Criteria {
    @ApiParam(value = "정렬 기준 \n" +
            "- 다음 중 하나로만 요청 가능\n" +
            "   - NAME_ASC: 이름 오름차순 \n" +
            "   - NAME_DESC: 이름 내림차순 \n" +
            "   - CREATED_AT_ASC: 등록순 \n" +
            "   - CREATED_AT_DESC: 최신순(등록순의 반대) \n")
    private Sort sort;

    @ApiParam(value = "필터링 기준 (리스트)\n" +
            "- 다음 중 선택하여 요청 가능 \n" +
            "   - IS_DELETED \n" +
            "   - REFRIGERATOR \n" +
            "   - CLOSET \n" +
            "   - TV \n" +
            "   - MICROWAVE \n" +
            "   - GAS_RANGE \n" +
            "   - INDUCTION \n" +
            "   - WATER_PURIFIER \n" +
            "   - AIR_CONDITIONER \n" +
            "   - WASHER \n" +
            "   - BED \n" +
            "   - DESK \n" +
            "   - SHOE_CLOSET \n" +
            "   - ELECTRONIC_DOOR_LOCKS \n" +
            "   - BIDET \n" +
            "   - VERANDA \n" +
            "   - ELEVATOR \n")
    private List<Filter> filter = new ArrayList<>();

    @ApiParam(value = "검색 대상 \n" +
            "- 기본값: NAME \n" +
            "- 다음중 하나로만 요청 가능 \n" +
            "   - NAME (이름 검색)")
    private SearchType searchType = SearchType.NAME;

    @ApiParam(value = "검색 문자열 \n" +
            "- null이 아닐 경우에는 문자열이 공백 문자로만 구성되어 있으면 안됨")
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
