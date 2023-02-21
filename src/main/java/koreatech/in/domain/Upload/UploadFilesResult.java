package koreatech.in.domain.Upload;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class UploadFilesResult {
    private final List<UploadFileResult> uploadFilesResult;

    public void append(UploadFileResult uploadFileResult) {
        uploadFilesResult.add(uploadFileResult);
    }
}
