package koreatech.in.dto.normal.upload.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
            example = "https://bucketname.ap-northeast-2.amazonaws.com/upload/domain/2000/00/00/d4cb13df-cf57-4612-b37d-80ecfa3f4621-1694847132589/image.jpg\n"
                    + "?x-amz-acl=public-read\n"
                    + "&X-Amz-Algorithm=AWS4-HMAC-SHA256\n"
                    + "&X-Amz-Date=20000000T000000Z\n"
                    + "&X-Amz-SignedHeaders=content-length%3Bcontent-type%3Bhost\n"
                    + "&X-Amz-Expires=7199&X-Amz-Credential=AKIA6BRP3Q6L5PUD5W5Q%2F20230916%2Fap-northeast-2%2Fs3%2Faws4_request\n"
                    + "&X-Amz-Signature=796esadfsadfxcv213f851431a88bc16c8db048f322b8993e21e4829c531",
            required = true
    )
    private final String preSignedUrl;

    @ApiModelProperty(notes = "Url 만료 일자", example = "2023-01-01 12:34:56", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final Date expirationDate;

    @ApiModelProperty(notes = "업로드한 파일을 가져올 때 사용하는 url",
            example = "https://static.koreatech.in/2023/09/01/uuid/example.png",
            required = true
    )
    private final String fileUrl;
}
