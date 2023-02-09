package koreatech.in.domain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Notice {
    public static final String CHANNEL = "channel";

    public static final String CHANNEL_EVENT_NOTIFICATION = "#코인_이벤트알림";

    public static final String USERNAME = "username";
    public static final String USERNAME_MEMBER_PLATFORM = "회원 플랫폼";

    public static final String TEXT = "text";
    public static final String ATTACHMENTS = "attachments";

    private final String channel;
    private final String username;
    private final String text;
    private final List<Object> attachments;
    private final String url;

    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();

        if (getChannel() != null) {
            params.put("channel", getChannel());
        }
        if (getUsername() != null) {
            params.put("username", getUsername());
        }
        if (getText() != null) {
            params.put("text", getText());
        }
        if (getAttachments() != null) {
            params.put("attachments", getAttachments());
        }

        return params;
    }

    public static Notice makeMemberPlatform(Object data, String url) {
        List<Object> attachment = new ArrayList<>();
        attachment.add(data);

        return Notice.builder()
                .channel(CHANNEL_EVENT_NOTIFICATION)
                .username(USERNAME_MEMBER_PLATFORM)
                .text(null)
                .attachments(attachment)
                .url(url)
                .build();
    }
}