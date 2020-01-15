package koreatech.in.exception;

import koreatech.in.domain.ErrorMessage;

public class UnauthorizeException extends ParentException {
    private ErrorMessage errorMessage;

    public UnauthorizeException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "UnauthorizeException{" +
                "errorMessage=" + errorMessage.getMap() +
                '}';
    }
}
