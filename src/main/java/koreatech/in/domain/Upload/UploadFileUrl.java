package koreatech.in.domain.Upload;

import koreatech.in.dto.upload.UploadFileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class UploadFileUrl {

    private final String fileUrl;

    public UploadFileResponse toDTO() {
        return UploadFileResponse.from(fileUrl);
    }
}
