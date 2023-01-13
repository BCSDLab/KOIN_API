package koreatech.in.domain.Upload;

import java.io.File;
import java.util.UUID;

public class UploadFileFullName {
    private static final String DASH = "-";

    //추후 파일명이 필요할 것 같아 필드를 둠.
    private final String name;
    private final FileExtension fileExtension;

    private UploadFileFullName(String originalFileName) {
        this.name = originalFileName;
        this.fileExtension = FileExtension.from(originalFileName);
    }

    public static UploadFileFullName from(String originalFileName) {
        return new UploadFileFullName(originalFileName);
    }

    public String getFileFullName() {
        return File.separator + getFileName() + fileExtension.getExtensionWithSeparator();
    }

    private String getFileName() {
        return UUID.randomUUID() + DASH + System.currentTimeMillis();
    }
}
