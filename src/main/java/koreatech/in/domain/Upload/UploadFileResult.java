package koreatech.in.domain.Upload;

import lombok.Getter;

@Getter
public class UploadFileResult {

    private final String fileUrl;
    private final String fileName;

    private UploadFileResult(String fileUrl, String fileName) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
    }

    public static UploadFileResult of(String domainName, UploadFile uploadFile) {
        return new UploadFileResult(domainName + UploadFileFullPath.SLASH +  uploadFile.getFullPath(), uploadFile.getFileName());
    }
}
