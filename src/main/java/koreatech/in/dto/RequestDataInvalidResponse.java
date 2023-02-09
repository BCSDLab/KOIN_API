package koreatech.in.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestDataInvalidResponse extends ExceptionResponse {
    private final List<String> violations;

    private RequestDataInvalidResponse(Integer code, String message, List<String> violations) {
        super(code, message);
        this.violations = violations;
    }

    public static RequestDataInvalidResponse of(Integer code, String message, List<String> violations) {
        return new RequestDataInvalidResponse(code, message, violations);
    }
}
