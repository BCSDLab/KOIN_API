package koreatech.in.dto.global;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttachmentUrlRequest {
//    public static final String STATIC_KOREATECH_IN = "static.koreatech.in";
    @NotBlank
    @URL(protocol = "https", regexp = ".*static\\.koreatech\\.in.*"
            , message = "코인 파일 저장 형식이 아닙니다.")
    @ApiModelProperty(notes = " \n"
            + "- not blank \n"
            + "- 코인 파일 저장 형식이어야 함"
            , required = true
            , example = "https://static.koreatech.in/assets/img/logo_white.png"
    )
    private String fileUrl;
}