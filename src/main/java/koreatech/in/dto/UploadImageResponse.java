package koreatech.in.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class UploadImageResponse {
    private String image_url;
}
