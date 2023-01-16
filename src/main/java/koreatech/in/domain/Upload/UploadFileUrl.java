package koreatech.in.domain.Upload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class UploadFileUrl {

    private final String fileUrl;
}
