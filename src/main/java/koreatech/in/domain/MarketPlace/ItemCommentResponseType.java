package koreatech.in.domain.MarketPlace;

public class ItemCommentResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "user_id",
            "is_deleted",
            "item_id",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }
}
