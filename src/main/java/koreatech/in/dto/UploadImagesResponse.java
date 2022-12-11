package koreatech.in.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class UploadImagesResponse {
    @ApiModelProperty(notes = "업로드된 이미지 url 리스트", example = "[\"https://static.koreatech.in/example1.png\", \"https://static.koreatech.in/example2.png\"]", required = true)
    private List<String> image_urls;
}
