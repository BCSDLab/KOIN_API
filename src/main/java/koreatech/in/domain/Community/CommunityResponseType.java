package koreatech.in.domain.Community;

import java.util.*;

public class CommunityResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
        "meta",
    };

    private static String[] toHotArticleArray = new String[]{
            "meta",
            "content",
            "user_id",
            "nickname",
            "ip",
            "is_solved",
            "is_deleted",
            "comment_count",
            "meta",
            "is_notice",
            "notice_article_id",
            "summary",
            "created_at",
            "updated_at",
    };


    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }

    public static String[] getHotArticleArray () {
        return toHotArticleArray;
    }
}
