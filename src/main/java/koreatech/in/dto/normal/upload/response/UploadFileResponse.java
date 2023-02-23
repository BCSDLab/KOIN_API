package koreatech.in.dto.normal.upload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadFileResponse {
    @ApiModelProperty(notes = "업로드된 파일 url",
            example = "https://static.koreatech.in/example.png",
            required = true
    )
    private final String fileUrl;

//    @ApiModelProperty(notes = "업로드된 파일 명",
//            example = "example.png",
//            required = true
//    )
//    private final String fileName;

    public static UploadFileResponse from(String fileUrl) {
        return new UploadFileResponse(fileUrl);
    }

//    public static UploadFileResponse of(String fileUrl, String fileName) {
//        return new UploadFileResponse(fileUrl, fileName);
//    }
}
