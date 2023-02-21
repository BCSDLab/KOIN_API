package koreatech.in.domain.Upload;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class UploadFilesResult {
    private final List<UploadFileResult> uploadFileResults;

    public void append(UploadFileResult uploadFileResult) {
        uploadFileResults.add(uploadFileResult);
    }
}
