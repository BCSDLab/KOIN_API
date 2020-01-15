package koreatech.in.domain.Search;

import java.util.HashMap;
import java.util.Map;

public class SearchEnum {
    public static final Map<Integer, Integer> articleBoard = new HashMap<Integer, Integer>() {{
        put(1, 5); // boardId 자유게시판 -> ServiceType FREE
        put(2, 6); // boardId 취업게시판 -> ServiceType JOB
        put(5, 0); // boardId 일반공지 -> ServiceType NOTICE_GENERAL
        put(6, 1); // boardId 장학공지 -> ServiceType NOTICE_SCHOLARSHIP
        put(7, 2); // boardId 학사공지 -> ServiceType NOTICE_ACADEMIC
        put(8, 3); // boardId 취업공지 -> ServiceType NOTICE_JOB
        put(9, 4); // boardId 코인공지 -> ServiceType NOTICE_KOIN
        put(10, 8); // boardId 질문게시판 -> ServiceType QUESTION
    }};

    public static final Map<Integer, String> articleBoardName = new HashMap<Integer, String>() {{
        put(1, "free"); // boardId 자유게시판 -> free
        put(2, "job"); // boardId 취업게시판 -> job
        put(5, "notice"); // boardId 일반공지 -> notice
        put(6, "notice"); // boardId 장학공지 -> notice
        put(7, "notice"); // boardId 학사공지 -> notice
        put(8, "notice"); // boardId 취업공지 -> notice
        put(9, "notice"); // boardId 코인공지 -> notice
        put(10, "question"); // boardId 질문게시판 -> question
    }};

    public enum ServiceType {
        NOTICE_GENERAL("일반공지"),
        NOTICE_SCHOLARSHIP("장학공지"),
        NOTICE_ACADEMIC("학사공지"),
        NOTICE_JOB("취업공지"),
        NOTICE_KOIN("코인공지"),
        FREE("자유게시판"),
        JOB("취업게시판"),
        ANONYMOUS("익명게시판"),
        QUESTION("질문게시판"),
        LOST("분실물"),
        MARKET("중고장터")
        ;

        private String typeText;
        private Integer typeId;

        ServiceType(String typeText) {
            this.typeText = typeText;
        }

        public String getTypeText() {
            return typeText;
        }

        public Integer getTypeId(Integer boardId) {
            return articleBoard.get(boardId);
        }
    }
}
