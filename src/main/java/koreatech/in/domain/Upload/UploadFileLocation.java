package koreatech.in.domain.Upload;

import lombok.Getter;

@Getter
public class UploadFileLocation {

    private final String fileUrl;
    private final String fileName;

    private UploadFileLocation(String fileUrl, String fileName) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
    }

    public static UploadFileLocation of(String domainName, UploadFile uploadFile) {
        return new UploadFileLocation(domainName + UploadFileFullPath.SLASH + uploadFile.getFullPath(),
                uploadFile.getFileName());
    }

    public static UploadFileLocation of(String domainName, UploadFileFullPath uploadFileFullPath) {
        return new UploadFileLocation(domainName + UploadFileFullPath.SLASH + uploadFileFullPath.unixValue(),
                uploadFileFullPath.getFileFullName());
    }

}
