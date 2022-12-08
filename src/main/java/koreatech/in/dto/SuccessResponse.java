package koreatech.in.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@NoArgsConstructor
public class SuccessResponse {
    @ApiModelProperty(notes = "API 수행 성공 여부", example = "true", required = true)
    private final Boolean success = true;
}