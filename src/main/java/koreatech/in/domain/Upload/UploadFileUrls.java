package koreatech.in.domain.Upload;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class UploadFileUrls {
    private final List<UploadFileUrl> uploadFileUrls;

    public void append(UploadFileUrl uploadFileUrl) {
        uploadFileUrls.add(uploadFileUrl);
    }
}
