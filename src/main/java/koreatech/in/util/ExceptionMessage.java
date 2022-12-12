package koreatech.in.util;

import lombok.Getter;

/**
 *   -2: 권한 없음
 *   -1: 데이터 유효성 검증 통과 X
 *   1 ~ 99: 공통
 *
 *   100번대: 상점 관련
 *   - 100 ~ 109: 상점
 *   - 110 ~ 119: 상점 카테고리
 *   - 120 ~ 129: 상점 메뉴
 *   - 130 ~ 139: 상점 메뉴 카테고리
 *
 *   200번대: BCSDLab 회원 관련
 *
 *   300번대: 복덕방 관련
 *
 */
@Getter
public enum ExceptionMessage {
    FORBIDDEN("권한이 없습니다.", -2),
    REQUEST_DATA_INVALID("요청 데이터가 유효하지 않습니다.", -1), // 메시지는 ErrorMessage 객체에 상황에 맞게 넣을 것

    // ======= 공통 =======
    PAGE_NOT_FOUND("유효하지 않은 페이지입니다.", 1),

    // ======= 상점 ========
    SHOP_NOT_FOUND("상점이 존재하지 않습니다.", 100),
    SHOP_NAME_DUPLICATE("상점 이름이 중복됩니다.", 101),
    SHOP_ALREADY_DELETED("상점이 이미 삭제되어있습니다.", 102),
    SHOP_NOT_DELETED("상점이 삭제되어 있지 않습니다.", 103),

    // ======= 상점 카테고리 =======
    SHOP_CATEGORY_NOT_FOUND("상점 카테고리가 존재하지 않습니다.", 110),
    SHOP_CATEGORY_NAME_DUPLICATE("상점 카테고리의 이름이 중복됩니다.", 111),
    SHOP_USING_CATEGORY_EXIST("상점 카테고리를 사용하고 있는 상점들이 있어 삭제할 수 없습니다.", 112),

    // ======= 상점 메뉴 =======
    SHOP_MENU_NOT_FOUND("메뉴가 존재하지 않습니다.", 120),
    SHOP_MENU_ALREADY_HIDDEN("메뉴가 이미 숨김처리 되어 있습니다.", 121),
    SHOP_MENU_NOT_HIDDEN("메뉴가 숨김처리 되어 있지 않습니다.", 122),

    // ======= 상점 메뉴 카테고리 =======
    SHOP_MENU_CATEGORY_NOT_FOUND("메뉴 카테고리가 존재하지 않습니다.", 130),
    SHOP_MENU_CATEGORY_NAME_DUPLICATE("메뉴 카테고리의 이름이 중복됩니다.", 131),
    SHOP_MENU_USING_CATEGORY_EXIST("메뉴 카테고리를 사용하고 있는 메뉴가 있어 삭제할 수 없습니다.", 132),
    SHOP_MENU_CATEGORY_MAXIMUM_EXCEED("등록할 수 있는 메뉴 카테고리의 최대 개수를 초과하였습니다.", 133),

    // ======= BCSDLab 회원 =======
    MEMBER_NOT_FOUND("존재하지 않는 BCSDLab 회원입니다.", 200),
    TRACK_NOT_FOUND("트랙이 존재하지 않습니다.", 201),
    MEMBER_ALREADY_DELETED("이미 삭제되어 있는 BCSDLab 회원입니다.", 202),
    MEMBER_NOT_DELETED("삭제되어 있는 BCSDLab 회원이 아닙니다.", 203),

    // ======= 복덕방 =======
    LAND_NOT_FOUND("존재하지 않는 집입니다.", 300),
    LAND_NAME_DUPLICATE("집 이름이 중복됩니다.", 301),
    LAND_ALREADY_DELETED("이미 삭제된 집입니다.", 302),
    LAND_NOT_DELETED("삭제되어 있는 집이 아닙니다.", 303);

    ExceptionMessage(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    private final String message;
    private final Integer code;
}
