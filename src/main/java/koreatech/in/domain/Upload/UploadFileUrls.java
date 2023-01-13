package koreatech.in.domain.Upload;

import java.util.List;
import java.util.stream.Collectors;
import koreatech.in.dto.upload.response.UploadFilesResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class UploadFileUrls {
    private final List<UploadFileUrl> uploadFileUrls;

    public UploadFilesResponse toDTO() {
        return UploadFilesResponse.from(uploadFileUrls.stream().map(UploadFileUrl::getFileUrl).collect(Collectors.toList()));
    }
}
