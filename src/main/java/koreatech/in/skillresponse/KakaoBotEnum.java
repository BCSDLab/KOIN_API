package koreatech.in.skillresponse;

public class KakaoBotEnum {
    public enum TemplateType {
        SIMPLETEXT("simpleText"), // 간단한 텍스트형 출력 요소
        SIMPLEIMAGE("simpleImage"), // 간단한 이미지형 출력 요소
        BASICCARD("basicCard"), // 기본 카드형 출력 요소
        COMMERCECARD("commerceCard"), // 커머스 카드형 출력 요소
        LISTCARD("listCard"), // 리스트 카드형 출력 요소
        CAROUSEL("carousel"), // 케로셀은 여러 장의 카드를 하나의 메세지에 일렬로 포함하는 타입
        QUICKREPLIES("quickReplies") // 제한적 선택지를 가진 응답이거나, 다음 발화에 대한 힌트를 줄 필요가 있을 때
        ; // https://i.kakao.com/docs/skill-response-format#outputs-%EC%B6%9C%EB%A0%A5-%EA%B7%B8%EB%A3%B9

        private String typeText;

        TemplateType(String typeText) {
            this.typeText = typeText;
        }

        public String getTypeText() {
            return typeText;
        }
    }

    public enum ButtonActionType {
        WEBLINK("webLink"), // 웹 브라우저를 열고 webLinkUrl 의 주소로 이동합니다.
        OSLINK("osLink"), // osLink 값에 따라서 웹의 주소로 이동하거나 앱을 실행합니다.
        MESSAGE("message"), // 사용자의 발화로 messageText를 실행합니다.
        PHONE("phone"), // phoneNumber에 있는 번호로 전화를 겁니다.
        BLOCK("block"), // blockId를 갖는 블록을 호출합니다.
        SHARE("share"), // 말풍선을 다른 유저에게 공유합니다. share action은 특히 케로셀을 공유해야 하는 경우 유용합니다.
        OPERATOR("operator") // 상담원 연결 기능을 제공합니다.
        ; // https://i.kakao.com/docs/skill-response-format#link

        private String typeText;

        ButtonActionType(String typeText) {
            this.typeText = typeText;
        }

        public String getTypeText() {
            return typeText;
        }
    }

    public enum QuickRepliesActionType {
        MESSAGE("message"), // 사용자의 발화로 messageText를 내보냅니다.
        BLOCK("block"), // 블록 연결시 사용자의 발화로 노출됩니다.
        ; // https://i.kakao.com/docs/skill-response-format#link

        private String typeText;

        QuickRepliesActionType(String typeText) {
            this.typeText = typeText;
        }

        public String getTypeText() {
            return typeText;
        }
    }

    public enum BusType {
        SHUTTLE("학교셔틀"),
        EXPRESS("고속버스"),
        CITY("시내버스")
        ;

        private String typeText;

        BusType(String typeText) { this.typeText = typeText; }

        public String getTypeText() { return typeText; }
    }
}
