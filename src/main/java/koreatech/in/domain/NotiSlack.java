package koreatech.in.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotiSlack {
    public static final String EMAIL_VERIFICATION_REQUEST_SUFFIX = "님이 이메일 인증을 요청하였습니다.";
    public static final String REGISTER_COMPLETE_SUFFIX = "님이 가입하셨습니다.";

    public static final String COLOR_GOOD = "good";


    private String color;

    private String author_name;

    private String title;

    private String title_link;

    private String text;
}
