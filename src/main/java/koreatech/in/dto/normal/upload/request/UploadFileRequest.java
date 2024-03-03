package koreatech.in.dto.normal.upload.request;

import java.io.IOException;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@RequiredArgsConstructor
public class UploadFileRequest {
    private final String domain;
    private final String originalFileName;
    private final byte[] data;

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
