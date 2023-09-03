package koreatech.in.dto.normal.upload.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PreSignedUrlRequest {
    private final String domainPath;
    private final String fileName;
}
