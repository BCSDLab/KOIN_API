package koreatech.in.dto.upload.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class UploadFilesRequest {
    private final static int FILE_MAX_SIZE = 10;
    private final List<UploadFileRequest> uploadFilesRequest;

    public List<UploadFileRequest> getUploadFilesRequest() {
        return Collections.unmodifiableList(uploadFilesRequest);
    }

    public static UploadFilesRequest of(List<MultipartFile> files,
                                                             String domain) {
        validation(files);

        List<UploadFileRequest> uploadFileRequests = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            uploadFileRequests.add(UploadFileRequest.of(domain, multipartFile));
        }

        return new UploadFilesRequest(uploadFileRequests);
    }

    private static void validation(List<MultipartFile> files) {
        validateEmpty(files);
        validateLengthOver(files);
    }

    private static void validateEmpty(List<MultipartFile> files) {
        if(files == null || files.isEmpty()) {
            throw new BaseException(ExceptionInformation.FILES_EMPTY);
        }
    }

    private static void validateLengthOver(List<MultipartFile> files) {
        if(files.size() > FILE_MAX_SIZE) {
            throw new BaseException(ExceptionInformation.FILES_LENGTH_OVER);
        }
    }
}
