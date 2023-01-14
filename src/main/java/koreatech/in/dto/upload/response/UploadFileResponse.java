package koreatech.in.dto.upload.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor(staticName = "from")
public final class UploadFileResponse {
    @ApiModelProperty(notes = "업로드된 파일 url", example = "https://static.koreatech.in/example.png", required = true)
    private final String file_url;
}
