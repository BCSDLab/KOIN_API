package koreatech.in.dto.upload.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class UploadFileResponse {
    @ApiModelProperty(notes = "업로드된 파일 url", example = "https://static.koreatech.in/example.png", required = true)
    private final String file_url;
}
