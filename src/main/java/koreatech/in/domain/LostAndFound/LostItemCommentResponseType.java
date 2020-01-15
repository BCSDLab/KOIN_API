package koreatech.in.domain.LostAndFound;

public class LostItemCommentResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "user_id",
            "is_deleted",
            "lost_item_id",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }
}
