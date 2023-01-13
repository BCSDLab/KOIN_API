package koreatech.in.dto.upload;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor(staticName = "from")
public class UploadFilesResponse {
    @ApiModelProperty(notes = "업로드된 파일 url 리스트", example = "[\"https://static.koreatech.in/example1.png\", \"https://static.koreatech.in/example2.png\"]", required = true)
    private final List<String> file_urls;
}
