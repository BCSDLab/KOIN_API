package koreatech.in.exception;

import koreatech.in.domain.ErrorMessage;

public class ValidationException  extends ParentException {
    private ErrorMessage errorMessage;

    public ValidationException (ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ValidationException {" +
                "errorMessage=" + errorMessage.getMap() +
                '}';
    }
}
