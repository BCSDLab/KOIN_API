package koreatech.in.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionInformation {
    REQUEST_DATA_INVALID("요청 데이터가 유효하지 않습니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY), // 메시지는 ErrorMessage 객체에 상황에 맞게 넣을 것

    // ======= 공통 =======
    PAGE_NOT_FOUND("유효하지 않은 페이지입니다.", 100002, HttpStatus.NOT_FOUND),

    // ======= 상점 ========
    SHOP_NOT_FOUND("상점이 존재하지 않습니다.", 104000, HttpStatus.NOT_FOUND),
    SHOP_NAME_DUPLICATE("상점 이름이 중복됩니다.", 104001, HttpStatus.CONFLICT),
    SHOP_ALREADY_DELETED("상점이 이미 삭제되어있습니다.", 104002, HttpStatus.CONFLICT),
    SHOP_NOT_DELETED("상점이 삭제되어 있지 않습니다.", 104003, HttpStatus.CONFLICT),
    SHOP_CATEGORY_NOT_FOUND("상점 카테고리가 존재하지 않습니다.", 104004, HttpStatus.NOT_FOUND),
    SHOP_CATEGORY_NAME_DUPLICATE("상점 카테고리의 이름이 중복됩니다.", 104005, HttpStatus.CONFLICT),
    SHOP_USING_CATEGORY_EXIST("상점 카테고리를 사용하고 있는 상점들이 있어 상점 카테고리를 삭제할 수 없습니다.", 104006, HttpStatus.CONFLICT),
    SHOP_MENU_NOT_FOUND("메뉴가 존재하지 않습니다.", 104007, HttpStatus.NOT_FOUND),
    SHOP_MENU_ALREADY_HIDDEN("메뉴가 이미 숨김처리 되어 있습니다.", 104008, HttpStatus.CONFLICT),
    SHOP_MENU_NOT_HIDDEN("메뉴가 숨김처리 되어 있지 않습니다.", 104009, HttpStatus.CONFLICT),
    SHOP_MENU_CATEGORY_NOT_FOUND("메뉴 카테고리가 존재하지 않습니다.", 104010, HttpStatus.NOT_FOUND),
    SHOP_MENU_CATEGORY_NAME_DUPLICATE("메뉴 카테고리의 이름이 중복됩니다.", 104011, HttpStatus.CONFLICT),
    SHOP_MENU_USING_CATEGORY_EXIST("메뉴 카테고리를 사용하고 있는 메뉴가 있어 메뉴 카테고리를 삭제할 수 없습니다.", 104012, HttpStatus.CONFLICT),
    SHOP_MENU_CATEGORY_MAXIMUM_EXCEED("등록할 수 있는 메뉴 카테고리의 최대 개수를 초과하였습니다.", 104013, HttpStatus.CONFLICT),

    // ======= 복덕방 =======
    LAND_NOT_FOUND("존재하지 않는 집입니다.", 107000, HttpStatus.NOT_FOUND),
    LAND_NAME_DUPLICATE("집 이름이 중복됩니다.", 107001, HttpStatus.CONFLICT),
    LAND_ALREADY_DELETED("이미 삭제된 집입니다.", 107002, HttpStatus.CONFLICT),
    LAND_NOT_DELETED("삭제되어 있는 집이 아닙니다.", 107003, HttpStatus.CONFLICT),

    // ======= 공통 =======
    DOMAIN_NOT_FOUND("존재하지 않는 도메인입니다.", 110000, HttpStatus.NOT_FOUND),
    FILE_INVALID("유효하지 않는 파일입니다.", 110001, HttpStatus.NOT_FOUND),


    // ======= BCSDLab 트랙 =======
    TRACK_NOT_FOUND("트랙이 존재하지 않습니다.", 201000, HttpStatus.NOT_FOUND),

    // ======= BCSDLab 회원 =======
    MEMBER_NOT_FOUND("존재하지 않는 BCSDLab 회원입니다.", 202000, HttpStatus.NOT_FOUND),
    MEMBER_ALREADY_DELETED("이미 삭제되어 있는 BCSDLab 회원입니다.", 202001, HttpStatus.CONFLICT),
    MEMBER_NOT_DELETED("삭제되어 있는 BCSDLab 회원이 아닙니다.", 202002, HttpStatus.CONFLICT);

    ExceptionInformation(String message, Integer code, HttpStatus httpStatus) {
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    private final String message;
    private final Integer code;
    private final HttpStatus httpStatus;
}
