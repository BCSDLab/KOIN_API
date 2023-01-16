package koreatech.in.dto.upload.request;

import java.io.IOException;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UploadFileRequest {
    private final String domain;
    private final String originalFileName;
    private final byte[] data;

    private UploadFileRequest(String domain, String originalFileName, byte[] data) {
        this.domain = domain;
        this.originalFileName = originalFileName;
        this.data = data;
    }

    public static UploadFileRequest of(String domain, MultipartFile multipartFile) {
        return new UploadFileRequest(domain,
                multipartFile.getOriginalFilename(),
                dataFor(multipartFile));
    }

    private static byte[] dataFor(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
    }
}
