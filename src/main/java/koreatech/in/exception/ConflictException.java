package koreatech.in.exception;

import koreatech.in.domain.ErrorMessage;

public class ConflictException extends ParentException {
    private ErrorMessage errorMessage;

    public ConflictException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ConflictException{" +
                "errorMessage=" + errorMessage.getMap() +
                '}';
    }
}
