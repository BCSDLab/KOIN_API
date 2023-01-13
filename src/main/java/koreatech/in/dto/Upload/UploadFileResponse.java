package koreatech.in.dto.Upload;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor(staticName = "from")
public class UploadFileResponse {
    @ApiModelProperty(notes = "업로드된 파일 url", example = "https://static.koreatech.in/example.png", required = true)
    private final String file_url;
}
