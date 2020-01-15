package koreatech.in.exception;

import koreatech.in.domain.ErrorMessage;

public class ForbiddenException extends ParentException {
    private ErrorMessage errorMessage;

    public ForbiddenException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ForbiddenException{" +
                "errorMessage=" + errorMessage.getMap() +
                '}';
    }
}
