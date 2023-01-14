package koreatech.in.dto.upload.request;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "from")
public final class UploadFilesRequest {
    private final List<UploadFileRequest> uploadFileRequests;

    public List<UploadFileRequest> getUploadFileRequests() {
        return Collections.unmodifiableList(uploadFileRequests);
    }
}
