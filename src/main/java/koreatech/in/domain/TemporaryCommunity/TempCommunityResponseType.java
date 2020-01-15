package koreatech.in.domain.TemporaryCommunity;

public class TempCommunityResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
        "password",
    };

    private static String[] toCommentArray = new String[] {
        "password",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }

    public static String[] getCommentArray () {
        return toCommentArray;
    }
}
