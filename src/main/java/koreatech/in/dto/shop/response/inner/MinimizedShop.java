package koreatech.in.dto.shop.response.inner;

import lombok.Getter;

import java.util.List;

/**
 *  어드민 페이지에서 페이지별 상점 목록을 불러올때 사용하는 응답 객체
 *  응답할 데이터가 적어 별개로 분리한 것임.
 *
 *  필요한 데이터
 *  - id
 *  - 이름
 *  - 전화번호
 *  - 카테고리 이름 리스트
 */

@Getter
public class MinimizedShop {
    private Integer id;
    private String name;
    private String phone;
    private List<String> category_names;
}
