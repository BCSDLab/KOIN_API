package koreatech.in.domain.Upload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UploadFileMetaData {
    private final String fileName;
    private final String contentType;
    private final Long contentLength;

}
