package koreatech.in.domain.Upload;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class UploadFile {

    private final UploadFileFullPath fullPath;
    private final byte[] data;

    public String getFullPath() {
        return fullPath.unixValue();
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fullPath.getFileFullName();
    }
}
