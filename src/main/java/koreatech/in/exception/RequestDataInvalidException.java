package koreatech.in.exception;

import lombok.Getter;

import java.util.List;

import static koreatech.in.exception.ExceptionInformation.REQUEST_DATA_INVALID;

@Getter
public class RequestDataInvalidException extends BaseException {
    private final List<String> violations;

    public RequestDataInvalidException(List<String> violations) {
        super(REQUEST_DATA_INVALID);
        this.violations = violations;
    }
}
