package koreatech.in.domain.Upload;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class UploadFilesResult {
    private final List<UploadFileLocation> uploadFilesResult;

    public void append(UploadFileLocation uploadFileLocation) {
        uploadFilesResult.add(uploadFileLocation);
    }
}
