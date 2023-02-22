package koreatech.in.dto.normal.upload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadFilesResponse {
    @ApiModelProperty(notes = "업로드된 파일 url 리스트",
            dataType = "[Lkoreatech.in.dto.normal.upload.response.UploadFileResponse;"
//            example = "[\"https://static.koreatech.in/example1.png\", \"https://static.koreatech.in/example2.png\"]", required = true
            )
    private final List<UploadFileResponse> fileUrls;

    public List<UploadFileResponse> getFileUrls() {
        return Collections.unmodifiableList(fileUrls);
    }
}
