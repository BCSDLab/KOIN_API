package koreatech.in.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class UploadImageResponse {
    @ApiModelProperty(notes = "업로드된 이미지 url", example = "https://static.koreatech.in/example.png", required = true)
    private String image_url;
}
