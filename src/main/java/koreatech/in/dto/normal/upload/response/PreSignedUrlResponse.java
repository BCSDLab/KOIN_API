package koreatech.in.dto.normal.upload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PreSignedUrlResponse {

    @ApiModelProperty(notes = "파일을 업로드할 수 있는 url",
            example = "static.koreatech.in/2023/09/01/uuid/example.png?x-amx-acl=public-read&X-Amz-Algorithm=AWS4-HMAC-SHA256",
            required = true
    )
    private final String preSignedUrl;

    @ApiModelProperty(notes = "업로드한 파일을 가져올 때 사용하는 url",
            example = "static.koreatech.in/2023/09/01/uuid/example.png",
            required = true
    )
    private final String fileUrl;
}
