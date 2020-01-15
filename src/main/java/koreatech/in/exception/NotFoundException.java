package koreatech.in.exception;

import koreatech.in.domain.ErrorMessage;

public class NotFoundException extends ParentException {
    private ErrorMessage errorMessage;

    public NotFoundException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "NotFoundException{" +
                "errorMessage=" + errorMessage.getMap() +
                '}';
    }
}
