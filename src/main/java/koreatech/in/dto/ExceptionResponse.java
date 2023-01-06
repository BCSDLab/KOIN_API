package koreatech.in.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    @ApiModelProperty(notes = "에러 코드", required = true)
    private Integer code;
    @ApiModelProperty(notes = "에러 메시지", required = true)
    private String message;

    public static ExceptionResponse of(Integer code, String message) {
        return new ExceptionResponse(code, message);
    }
}
