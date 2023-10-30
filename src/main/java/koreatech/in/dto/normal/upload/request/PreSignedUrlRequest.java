package koreatech.in.dto.normal.upload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
public class PreSignedUrlRequest {

    @NotBlank(message = "파일 이름은 필수입니다.")
    @Size(max = 256, message = "파일 이름은 256자 이내여야 합니다.")
    @ApiModelProperty(notes = "파일 이름 \n"
            + "- not null \n"
            + "256자 이내여야 함"
            , required = true
            , example = "apple.png"
    )
    private String fileName;

    @NotBlank(message = "컨텐츠 타입은 필수입니다.")
    @Size(max = 100, message = "컨텐츠 타입은 100자 이내여야 합니다.")
    @ApiModelProperty(notes = "컨텐츠 타입 \n"
            + "- not null \n"
            + "100자 이내여야 함"
            , required = true
            , example = "image/png"
    )
    private String contentType;

    @ApiModelProperty(notes = "본문(파일)의 길이(크기) \n"
            + "- not null \n"
            + "0 ~ 100mb 이내여야 함"
            , required = true
            , example = "100000"
    )
    @NotNull(message = "본문(파일)의 길이(크기)는 필수입니다.")
    @Min(value = 0, message = "본문(파일)의 길이(크기)는 0 이상이어야 합니다.")
    @Max(value = 100_000_000, message = "본문(파일)의 길이(크기)는 100000000(100mb) 이하여야 합니다.")
    private Long contentLength;
}
