package koreatech.in.domain.Upload;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "from")
public class FileExtension {
    private final String originalFileName;

    static private final String EXTENSION_SEPARATOR = ".";
    static private final String NO_EXTENSION = "";

    public String getExtensionWithSeparator() {

        int extensionStartIndex = originalFileName.lastIndexOf(EXTENSION_SEPARATOR) + 1;

        if (!hasExtension(extensionStartIndex)) {
            return NO_EXTENSION;
        }

        return EXTENSION_SEPARATOR + originalFileName.substring(extensionStartIndex);
    }

    //"test", ".gitignore" 같은 경우 false 반환
    private boolean hasExtension(int extensionStartIndex) {
        return extensionStartIndex > 1;
    }

}