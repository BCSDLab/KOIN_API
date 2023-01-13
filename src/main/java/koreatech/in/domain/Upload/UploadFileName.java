package koreatech.in.domain.Upload;

import java.io.File;
import java.util.UUID;

public class UploadFileName {
    private static final String DASH = "-";

    private final FileExtension fileExtension;

    public UploadFileName(String originalFileName) {
        this.fileExtension = FileExtension.from(originalFileName);
    }

    public String getFileFullName() {
        return File.separator + getFileName() + fileExtension.getExtensionWithSeparator();
    }

    private String getFileName() {
        return UUID.randomUUID() + DASH + System.currentTimeMillis();
    }
}
