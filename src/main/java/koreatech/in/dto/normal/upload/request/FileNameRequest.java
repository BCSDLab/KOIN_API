package koreatech.in.dto.normal.upload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
public class FileNameRequest {

    @NotBlank(message = "파일 이름은 필수입니다.")
    @Size(max = 256, message = "파일 이름은 256자 이내여야 합니다.")
    @ApiModelProperty(notes = "파일 이름 \n"
            + "- not null \n"
            + "256자 이내여야 함"
            , required = true
            , example = "apple.png"
    )
    private String fileName;
}
