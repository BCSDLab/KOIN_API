package koreatech.in.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ExceptionInformation {
    // 코인 리뉴얼 Error code 정리: https://docs.google.com/spreadsheets/d/1yWYFqGOPA_6ZQQ4Mb7bvOyUMH9NbyBnIuJ8jz6XkwUA/edit#gid=0
    // ======= 공통 =======
    REQUEST_DATA_INVALID("요청 데이터가 유효하지 않습니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY), // 메시지는 ErrorMessage 객체에 상황에 맞게 넣을 것
    BAD_ACCESS("잘못된 접근입니다.", 100001, HttpStatus.UNAUTHORIZED),
    PAGE_NOT_FOUND("유효하지 않은 페이지입니다.", 100002, HttpStatus.NOT_FOUND),
    FORBIDDEN("권한이 없습니다.", 100003, HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("토큰의 유효시간이 만료되었습니다. 다시 로그인해주세요.", 100004, HttpStatus.UNAUTHORIZED),
//    ACCESS_TOKEN_CHANGED("토큰이 변경되었습니다. 다시 로그인해주세요.", 100005, HttpStatus.UNAUTHORIZED),

    // ======= 코인 회원 (학생, 사장님) =======
    USER_NOT_FOUND("회원이 존재하지 않습니다.", 101000, HttpStatus.UNAUTHORIZED),
    PASSWORD_DIFFERENT("비밀번호가 일치하지 않습니다.", 101001, HttpStatus.UNAUTHORIZED),
    NICKNAME_DUPLICATE("이미 존재하는 닉네임입니다.", 101002, HttpStatus.CONFLICT),
    INQUIRED_USER_NOT_FOUND("조회한 회원이 존재하지 않습니다.", 101003, HttpStatus.NOT_FOUND),
    USER_HAS_WITHDRAWN("이미 탈퇴한 회원입니다.", 101004, HttpStatus.CONFLICT),
    USER_HAS_NOT_WITHDRAWN("탈퇴한 회원이 아닙니다.", 101005, HttpStatus.CONFLICT),
    IMPOSSIBLE_UNDELETE_USER_BECAUSE_SAME_ACCOUNT_EXIST("탈퇴하지 않은 회원 중 같은 ID를 가진 회원이 있어, 탈퇴를 해제할 수 없습니다.", 101006, HttpStatus.CONFLICT),
    IMPOSSIBLE_UNDELETE_USER_BECAUSE_SAME_EMAIL_EXIST("탈퇴하지 않은 회원 중 같은 이메일을 가진 회원이 있어, 탈퇴를 해제할 수 없습니다.", 101007, HttpStatus.CONFLICT),
    EMAIL_ADDRESS_INVALID("유효하지 않는 이메일 주소입니다.", 101008, HttpStatus.UNPROCESSABLE_ENTITY),
    EMAIL_DOMAIN_INVALID("유효하지 않는 이메일 도메인입니다.", 101009, HttpStatus.UNPROCESSABLE_ENTITY),
    EMAIL_ADDRESS_SAVE_EXPIRED("저장기간이 만료된 이메일 주소입니다. 다시 회원가입을 시도해주세요.", 101010, HttpStatus.GONE),
    CERTIFICATION_CODE_ALREADY_COMPLETED("해당 이메일은 이미 인증 완료되었습니다.", 101011, HttpStatus.CONFLICT),
    CERTIFICATION_CODE_NOT_COMPLETED("해당 이메일은 인증되지 않았습니다.", 101012, HttpStatus.CONFLICT),
    EMAIL_DUPLICATED("이미 존재하는 이메일 주소입니다. 다른 이메일 주소를 사용해주세요.", 101013, HttpStatus.CONFLICT),
    EMAIL_DOMAIN_IS_NOT_PORTAL_DOMAIN("한국기술교육대학교 포탈의 이메일 형식('koreatech.ac.kr')이 아닙니다.", 101014, HttpStatus.UNPROCESSABLE_ENTITY),
    STUDENT_NUMBER_INVALID("학생의 학번 형식이 아닙니다.", 101015, HttpStatus.UNPROCESSABLE_ENTITY),
    STUDENT_MAJOR_INVALID("학생의 전공 형식이 아닙니다.", 101016, HttpStatus.UNPROCESSABLE_ENTITY),
    NOT_STUDENT("회원의 신원이 학생이 아닙니다.", 101017, HttpStatus.CONFLICT),
    NOT_OWNER("회원의 신원이 사장님이 아닙니다.", 101018, HttpStatus.CONFLICT),

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
    //TODO 23.02.11. 박한수 파일 업로드 API에서, 파일목록(List<File~>) {다중 파일 업로드시 request body에 매핑되는 collection} 을 list를 필드로 갖는 객체로 변경하여, 이 객체에 annotation validation을 적용할 수 있는지 보기.
    FILES_EMPTY("파일목록이 비어있습니다.", 110002, HttpStatus.UNPROCESSABLE_ENTITY),
    FILES_LENGTH_OVER("파일목록의 길이가 최대보다 큽니다.", 110003, HttpStatus.CONFLICT),
    UNEXPECTED_FILE_CONTENT_TYPE("도메인이 허용하지 않는 콘텐츠 타입입니다.", 110004, HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    FILE_SIZE_OVER("파일의 크기가 도메인의 허용 크기보다 큽니다.", 110005, HttpStatus.REQUEST_ENTITY_TOO_LARGE),

    // ======= 사장님 =======
    CERTIFICATION_CODE_EXPIRED("코드 인증 기한이 경과되었습니다.", 121001, HttpStatus.GONE),
    CERTIFICATION_CODE_INVALID("인증 코드가 일치하지 않습니다.", 121002, HttpStatus.UNPROCESSABLE_ENTITY),
    OWNER_ATTACHMENT_NOT_FOUND("존재하지 않는 첨부파일입니다.", 121003, HttpStatus.NOT_FOUND),

    // ======= 파일 업로드 =======
    VERSION_TYPE_NOT_FOUND("존재하지 않는 버전의 타입입니다. 다시 시도해주세요.", 122000, HttpStatus.NOT_FOUND),
    VERSION_NOT_FOUND("타입에 해당하는 버전이 없습니다.", 122001, HttpStatus.NOT_FOUND),


    // ======= BCSDLab 트랙 =======
    TRACK_NOT_FOUND("트랙이 존재하지 않습니다.", 201000, HttpStatus.NOT_FOUND),

    // ======= BCSDLab 회원 =======
    MEMBER_NOT_FOUND("존재하지 않는 BCSDLab 회원입니다.", 202000, HttpStatus.NOT_FOUND),
    MEMBER_ALREADY_DELETED("이미 삭제되어 있는 BCSDLab 회원입니다.", 202001, HttpStatus.CONFLICT),
    MEMBER_NOT_DELETED("삭제되어 있는 BCSDLab 회원이 아닙니다.", 202002, HttpStatus.CONFLICT),


    // ======= 422 (unprocessable entity) exception =======
    SEARCH_QUERY_LENGTH_AT_LEAST_1("검색 내용의 최소 길이는 1입니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    SEARCH_QUERY_MUST_NOT_BE_BLANK("검색 내용은 공백 문자로만 이루어져 있으면 안됩니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    NICKNAME_SHOULD_NOT_BE_NULL("닉네임은 필수입니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    NICKNAME_LENGTH_AT_LEAST_1("닉네임의 최소 길이는 1입니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    NICKNAME_MUST_NOT_BE_BLANK("닉네임은 공백 문자로만 이루어져 있으면 안됩니다,", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    NICKNAME_MAXIMUM_LENGTH_IS_10("닉네임은 최대 10자입니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    LENGTH_OF_OPENS_MUST_BE_7("운영 시간 정보의 길이는 7이어야 합니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    TIME_INFORMATION_IS_REQUIRED_UNLESS_CLOSED("휴무가 아니라면 여는 시간과 닫는 시간 정보는 필수입니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    DUPLICATE_DAY_OF_WEEK_INFORMATION_EXISTS("중복되는 요일이 존재합니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    PRICE_OF_MENU_IS_REQUIRED("메뉴의 가격 정보는 필수입니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    DUPLICATE_OPTIONS_EXIST_IN_MENU("메뉴에서 중복되는 옵션명이 있습니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    SEARCH_TYPE_SHOULD_NOT_BE_NULL_WHEN_QUERY_IS_NOT_NULL("검색 내용이 존재할 경우 검색 대상은 필수입니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY),
    UPLOAD_FILE_URL_INVALID("코인 파일 저장 형식(static.koreatech.in)이 아닙니다.", 100000, HttpStatus.UNPROCESSABLE_ENTITY);

    ExceptionInformation(String message, Integer code, HttpStatus httpStatus) {
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    private final String message;
    private final Integer code;
    private final HttpStatus httpStatus;
}
