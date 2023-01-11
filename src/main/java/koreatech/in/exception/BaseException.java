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

    /**
     *
     * @param message ExceptionInformation Enum에 저장된 message가 아닌, 직접 입력하여 적용할 message
     * @param exceptionInformation ExceptionInformation Enum
     */
    public BaseException(String message, ExceptionInformation exceptionInformation) {
        super(message);
        this.errorCode = exceptionInformation.getCode();
        this.httpStatus = exceptionInformation.getHttpStatus();
    }
}
