package koreatech.in.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequestDataInvalidResponse {
    @ApiModelProperty(notes = "에러 코드", required = true)
    private Integer code;
    @ApiModelProperty(notes = "에러 메시지", required = true)
    private List<String> message;

    public RequestDataInvalidResponse of(Integer code, List<String> message) {
        return new RequestDataInvalidResponse(code, message);
    }
}
