package koreatech.in.domain.Community;

public class BoardResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "is_deleted",
            "created_at",
            "updated_at",
            "parent_id",
            "children",
    };

    private static String[] toArrayList = new String[] {
            "class",
            "is_deleted",
            "created_at",
            "updated_at",
            "parent_id",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }

    public static String[] getArrayList () {
        return toArrayList;
    }
}
