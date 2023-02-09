package koreatech.in.exception;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static koreatech.in.exception.ExceptionInformation.REQUEST_DATA_INVALID;

@Getter
public class RequestDataInvalidException extends BaseException {
    private final List<String> violations;

    public RequestDataInvalidException(String... violations) {
        super(REQUEST_DATA_INVALID);
        this.violations = Arrays.stream(violations).collect(Collectors.toList());
    }

    public RequestDataInvalidException(List<String> violations) {
        super(REQUEST_DATA_INVALID);
        this.violations = violations;
    }
}
