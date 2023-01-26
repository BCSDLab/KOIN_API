package koreatech.in.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionInformation {
    // ======= 공통 =======
    REQUEST_DATA_INVALID("요청 데이터가 유효하지 않습니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY), // 메시지는 ErrorMessage 객체에 상황에 맞게 넣을 것
    BAD_ACCESS("잘못된 접근입니다.", 100001, HttpStatus.UNAUTHORIZED),
    PAGE_NOT_FOUND("유효하지 않은 페이지입니다.", 100002, HttpStatus.NOT_FOUND),
    FORBIDDEN("권한이 없습니다.", 100003, HttpStatus.FORBIDDEN),
    ACCESS_TOKEN_EXPIRED("토큰의 유효시간이 만료되었습니다. 다시 로그인해주세요.", 100004, HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_CHANGED("토큰이 변경되었습니다. 다시 로그인해주세요.", 100005, HttpStatus.UNAUTHORIZED),

    // ======= 코인 회원 (학생, 사장님) =======
    USER_NOT_FOUND("회원이 존재하지 않습니다.", 101000, HttpStatus.UNAUTHORIZED),
    PASSWORD_DIFFERENT("비밀번호가 일치하지 않습니다.", 101001, HttpStatus.UNAUTHORIZED),
    NICKNAME_DUPLICATE("이미 존재하는 닉네임입니다.", 101002, HttpStatus.CONFLICT),
    USER_IS_NOT_STUDENT("회원의 신원이 학생이 아닙니다.", 101003, HttpStatus.CONFLICT),
    USER_IS_NOT_OWNER("회원의 신원이 사장님이 아닙니다.", 101004, HttpStatus.CONFLICT),
    USER_HAS_WITHDRAWN("탈퇴한 회원입니다.", 101005, HttpStatus.CONFLICT),
    USER_ALREADY_HAS_ADMIN_AUTHORITY("회원의 어드민 권한이 이미 존재합니다.", 101006, HttpStatus.CONFLICT),
    USER_HAS_NOT_COMPLETED_EMAIL_AUTHENTICATION("회원이 아직 이메일 인증을 완료하지 않았습니다.",101007, HttpStatus.CONFLICT),
    INQUIRED_USER_NOT_FOUND("조회한 회원이 존재하지 않습니다.", 101008, HttpStatus.NOT_FOUND),

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

    // ======= 파일 업로드 =======
    DOMAIN_NOT_FOUND("존재하지 않는 도메인입니다.", 110000, HttpStatus.NOT_FOUND),
    FILE_INVALID("유효하지 않는 파일입니다.", 110001, HttpStatus.UNPROCESSABLE_ENTITY),
    FILES_EMPTY("파일목록이 비어있습니다.", 110002, HttpStatus.UNPROCESSABLE_ENTITY),
    FILES_LENGTH_OVER("파일목록의 길이가 최대보다 큽니다.", 110003, HttpStatus.CONFLICT),


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
