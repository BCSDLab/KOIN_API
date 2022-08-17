package koreatech.in.domain.user;

public class UserResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "password",
            "remember_token",
            "created_at",
            "updated_at",
            "is_authed",
            "auth_token",
            "auth_expired_at",
            "reset_token",
            "reset_expired_at",
            "last_logged_at",
            "is_deleted",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }
}
