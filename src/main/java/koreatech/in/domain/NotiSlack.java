package koreatech.in.domain;

import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotiSlack {
    public static final String EMAIL_VERIFICATION_REQUEST_SUFFIX = "님이 이메일 인증을 요청하였습니다.";
    public static final String REGISTER_COMPLETE_SUFFIX = "님이 가입하셨습니다.";
    public static final String DELETE_COMPLETE_SUFFIX = "님이 탈퇴하셨습니다.";
    public static final String OWNERSHOP_REQUEST_SUFFIX = "님이 상점연결을 요청하셨습니다.";

    public static final String COLOR_GOOD = "good";
    public static final String BACKTICK = "`";
    public static final String OPENING_BRACKET = "(";
    public static final String CLOSING_BRACKET = ")";


    private String color;

    private String author_name;

    private String title;

    private String title_link;

    private String text;


    public static NotiSlack emailVerificationNotiSlack(User user) {
        return NotiSlack.builder()
                .color(NotiSlack.COLOR_GOOD)
                .text(makeText(user, NotiSlack.EMAIL_VERIFICATION_REQUEST_SUFFIX))
                .build();
    }

    public static NotiSlack registerCompleteNotiSlack(User user) {
        return NotiSlack.builder()
                .color(NotiSlack.COLOR_GOOD)
                .text(makeText(user, NotiSlack.REGISTER_COMPLETE_SUFFIX))
                .build();
    }

    public static NotiSlack deleteCompleteNotiSlack(User user) {
        return NotiSlack.builder()
                .color(NotiSlack.COLOR_GOOD)
                .text(makeText(user, NotiSlack.DELETE_COMPLETE_SUFFIX))
                .build();
    }

    public static NotiSlack ownerShopRequestNotiSlack(User user) {
        return NotiSlack.builder()
                .color(NotiSlack.COLOR_GOOD)
                .text(makeText(user, OWNERSHOP_REQUEST_SUFFIX))
                .build();
    }

    private static String makeText(User user, String messageSuffix) {
        return wrapWithBacktick(user.getEmail() + DelimitedText(user))  + messageSuffix;
    }

    private static String wrapWithBacktick(String text) {
        return BACKTICK + text + BACKTICK;
    }

    private static String DelimitedText(User user) {
        String userTypeText = UserType.mappingFor(user).getText();
        return OPENING_BRACKET + userTypeText + CLOSING_BRACKET;
    }
}
