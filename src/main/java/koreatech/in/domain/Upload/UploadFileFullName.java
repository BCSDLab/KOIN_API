package koreatech.in.domain.Upload;

public class UploadFileFullName {

    static private final String NO_EXTENSION = "";
    static private final String EXTENSION_SEPARATOR = ".";
    public static final int NOT_FOUND_INDEX = -1;
    public static final int ZERO_INDEX = 0;
    //추후 파일명이 필요할 것 같아 필드를 둠.
    private final String name;
    private final String extensionWithSeparator;

    private UploadFileFullName(String name, String extensionWithSeparator) {
        this.name = name;
        this.extensionWithSeparator = extensionWithSeparator;
    }

    public static UploadFileFullName from(String originalFileName) {
        return new UploadFileFullName(makeName(originalFileName), makeExtensionWithSeparator(originalFileName));
    }

    public String getFileFullName() {
        return name + extensionWithSeparator;
    }

    private static String makeName(String originalFileName) {
        return originalFileName.substring(ZERO_INDEX, getExtensionSeparatorIndex(originalFileName));
    }

    private static String makeExtensionWithSeparator(String originalFileName) {
        if (!hasExtension(originalFileName)) {
            return NO_EXTENSION;
        }

        return originalFileName.substring(getExtensionSeparatorIndex(originalFileName));
    }

    private static int getExtensionSeparatorIndex(String originalFileName) {
        return originalFileName.lastIndexOf(EXTENSION_SEPARATOR);
    }

    //"test" -> false, ".gitignore" -> true, "picture.png" -> true
    private static boolean hasExtension(String originalFileName) {
        return getExtensionSeparatorIndex(originalFileName) != NOT_FOUND_INDEX;
    }
}
