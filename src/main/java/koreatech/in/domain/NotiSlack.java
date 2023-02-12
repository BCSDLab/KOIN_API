package koreatech.in.domain;

import koreatech.in.domain.User.User;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotiSlack {
    public static final String EMAIL_VERIFICATION_REQUEST_SUFFIX = "님이 이메일 인증을 요청하였습니다.";
    public static final String REGISTER_COMPLETE_SUFFIX = "님이 가입하셨습니다.";

    public static final String COLOR_GOOD = "good";

    public static final String STUDENT_MESSAGE = "(학생)";
    public static final String OWNER_MESSAGE = "(사장님)";
    public static final String MEMBER_MESSAGE = "(회원)";


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

    private static String makeText(User user, String messageSuffix) {
        return user.getEmail() + DelimitedText(user) + messageSuffix;
    }

    private static String DelimitedText(User user) {
        if(user instanceof Student) {
            return STUDENT_MESSAGE;
        }
        if(user instanceof Owner) {
            return OWNER_MESSAGE;
        }

        return MEMBER_MESSAGE;
    }

}
