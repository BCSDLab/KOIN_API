package koreatech.in.domain.TemporaryCommunity;

public class TempArticleResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "is_deleted",
            "password",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }
}
