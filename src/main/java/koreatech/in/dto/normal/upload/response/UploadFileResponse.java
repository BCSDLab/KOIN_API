package koreatech.in.dto.normal.upload.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadFileResponse {
    @ApiModelProperty(notes = "업로드된 파일 url",
            example = "https://static.koreatech.in/example.png",
            required = true
    )
    private final String file_url;

//    @ApiModelProperty(notes = "업로드된 파일 명",
//            example = "example.png",
//            required = true
//    )
//    private final String file_name;

    public static UploadFileResponse from(String file_url) {
        return new UploadFileResponse(file_url);
    }

//    public static UploadFileResponse of(String file_url, String file_name) {
//        return new UploadFileResponse(file_url, file_name);
//    }
}
