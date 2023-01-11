package koreatech.in.exception;

import koreatech.in.domain.ErrorMessage;

public class BadRequestException extends ParentException {
    private ErrorMessage errorMessage;

    public BadRequestException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "BadRequestException{" +
                "errorMessage=" + errorMessage.getMap() +
                '}';
    }
}
