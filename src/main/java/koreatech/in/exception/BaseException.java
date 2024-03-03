package koreatech.in.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends ParentException {
    private final Integer errorCode;
    private final HttpStatus httpStatus;

    public BaseException(ExceptionInformation exceptionInformation) {
        super(exceptionInformation.getMessage());
        this.errorCode = exceptionInformation.getCode();
        this.httpStatus = exceptionInformation.getHttpStatus();
    }
}
