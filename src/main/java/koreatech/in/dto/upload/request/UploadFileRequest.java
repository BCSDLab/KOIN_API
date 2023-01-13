package koreatech.in.dto.upload.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor(staticName = "of")
public class UploadFileRequest {
    private final String domain;
    private final String originalFileName;
    private final byte[] data;

}