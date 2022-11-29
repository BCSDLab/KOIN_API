package koreatech.in.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotiSlack {
    private String color;

    private String author_name;

    private String title;

    private String title_link;

    private String text;
}
