package koreatech.in.dto.normal.upload.response;

import io.swagger.annotations.ApiModelProperty;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UploadFilesResponse {
    @ApiModelProperty(notes = "업로드된 파일 url 리스트", example = "[\"https://static.koreatech.in/example1.png\", \"https://static.koreatech.in/example2.png\"]", required = true)
    private final List<String> file_urls;

    public List<String> getFile_urls() {
        return Collections.unmodifiableList(file_urls);
    }
}
