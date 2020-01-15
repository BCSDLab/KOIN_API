package koreatech.in.domain.Circle;

public class CircleResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "is_deleted",
            "created_at",
            "updated_at",
    };

    private static String[] toArrayList = new String[] {
            "class",
            "is_deleted",
            "created_at",
            "updated_at",
            "description",
            "link_urls",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }

    public static String[] getArrayList () { return toArrayList; }
}
