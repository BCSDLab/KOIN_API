package koreatech.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class UploadImagesResponse {
    private List<String> image_urls;
}
