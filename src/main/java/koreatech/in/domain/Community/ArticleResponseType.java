package koreatech.in.domain.Community;

import java.util.*;

public class ArticleResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "is_deleted",
            "user_id",
            "ip",
            "summary",
            "notice_article_id",
            "meta",
    };

    private static String[] toHotArticleArray = new String[]{
            "class",
            "meta",
            "content",
            "user_id",
            "nickname",
            "ip",
            "is_solved",
            "is_deleted",
            "meta",
            "is_notice",
            "notice_article_id",
            "summary",
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
