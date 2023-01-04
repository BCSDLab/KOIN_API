package koreatech.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private Integer code;
    private String message;

    public static ExceptionResponse of(Integer code, String message) {
        return new ExceptionResponse(code, message);
    }
}
