package koreatech.in.dto.upload.request;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor(staticName = "of")
public class UploadFilesRequest {
    private final List<UploadFileRequest> uploadFileRequests;

}
