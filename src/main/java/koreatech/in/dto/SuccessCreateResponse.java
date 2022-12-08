package koreatech.in.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public class SuccessCreateResponse extends SuccessResponse {
    @ApiModelProperty(notes = "생성된 레코드의 고유 id", example = "1", required = true)
    private Integer id; // 생성된 레코드 id
}