package koreatech.in.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ParentException extends RuntimeException {
    public ParentException(String message) {
        super(message);
    }
}
