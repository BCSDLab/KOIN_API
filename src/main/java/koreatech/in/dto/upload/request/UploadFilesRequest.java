package koreatech.in.dto.upload.request;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "from")
public final class UploadFilesRequest {
    private final List<UploadFileRequest> uploadFilesRequest;

    public List<UploadFileRequest> getUploadFilesRequest() {
        return Collections.unmodifiableList(uploadFilesRequest);
    }
}
