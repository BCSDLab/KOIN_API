package koreatech.in.domain.Upload;

import java.io.File;
import java.util.UUID;

public class UploadFileFullName {
    private static final String DASH = "-";

    //추후 파일명이 필요할 것 같아 필드를 둠.
    private final String originalName;
    private final FileExtensionName fileExtensionName;
    private final String fullName;

    private UploadFileFullName(String originalFileName) {
        this.originalName = originalFileName;
        this.fileExtensionName = FileExtensionName.from(originalFileName);
        this.fullName = makeFileFullName();

    }

    public static UploadFileFullName from(String originalFileName) {
        return new UploadFileFullName(originalFileName);
    }

    public String makeFileFullName() {
        return File.separator + makeFileName() + fileExtensionName.getExtensionWithSeparator();
    }

    private String makeFileName() {
        return UUID.randomUUID() + DASH + System.currentTimeMillis();
    }

    public String getFileFullName() {
        return this.fullName;
    }
}
