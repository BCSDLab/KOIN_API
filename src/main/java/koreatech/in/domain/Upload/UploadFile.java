package koreatech.in.domain.Upload;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

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

    public static UploadFile of(MultipartFile multipartFile, String domainPath) throws IOException {
        UploadFileFullPath uploadFileFullPath = UploadFileFullPath.of(domainPath, multipartFile.getOriginalFilename());

        return new UploadFile(uploadFileFullPath, multipartFile.getBytes());
    }
}
