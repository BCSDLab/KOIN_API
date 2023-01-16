package koreatech.in.dto.upload.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class UploadFilesRequest {
    private final List<UploadFileRequest> uploadFilesRequest;

    public List<UploadFileRequest> getUploadFilesRequest() {
        return Collections.unmodifiableList(uploadFilesRequest);
    }


    public static UploadFilesRequest from(List<MultipartFile> files,
                                                             String domain) {

        List<UploadFileRequest> uploadFileRequests = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            uploadFileRequests.add(UploadFileRequest.of(domain, multipartFile));
        }

        return new UploadFilesRequest(uploadFileRequests);
    }
}
