package koreatech.in.exception;

import koreatech.in.domain.ErrorMessage;

public class PreconditionFailedException extends ParentException {
    private ErrorMessage errorMessage;

    public PreconditionFailedException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "PreconditionFailedException{" +
                "errorMessage=" + errorMessage.getMap() +
                '}';
    }
}
