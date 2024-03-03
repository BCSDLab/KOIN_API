package koreatech.in.domain.Upload;

import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContentType {

    private static final String SLASH = "/";
    private static final String WILD_CARD = "*";
    public static final int BEGIN_INDEX = 0;
    public static final int INVALID_INDEX = -1;

    private final String type;
    private final String subType;

    public static ContentType from(String contentType) {
        validates(contentType);
        int slashIndex = contentType.indexOf(SLASH);

        return new ContentType(contentType.substring(BEGIN_INDEX, slashIndex),
                contentType.substring(slashIndex + SLASH.length()));
    }

    public void validateAcceptable(ContentType candidate) {
        validateAcceptType(candidate);
        validateAcceptSubType(candidate);
    }

    private static void validates(String contentType) {
        if(contentType.indexOf(SLASH) == INVALID_INDEX) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
    }

    public boolean isAcceptable(ContentType candidate) {
        try {
            validateAcceptable(candidate);
        } catch (BaseException exception) {
            return false;
        }
        return true;
    }

    private boolean isTypeWildCard() {
        return type.equals(WILD_CARD);
    }

    private boolean isSubTypeWildCard() {
        return subType.equals(WILD_CARD);
    }

    private boolean isEqualType(ContentType other) {
        return type.equals(other.type);
    }

    private boolean isEqualSubType(ContentType other) {
        return subType.equals(other.subType);
    }

    private void validateAcceptSubType(ContentType candidate) {
        if(isSubTypeWildCard()) {
            return;
        }
        if(isEqualSubType(candidate)) {
            return;
        }
        throw new BaseException(ExceptionInformation.UNEXPECTED_FILE_CONTENT_TYPE);
    }

    private void validateAcceptType(ContentType candidate) {
        if(isTypeWildCard()) {
            return;
        }
        if(isEqualType(candidate)) {
            return;
        }

        throw new BaseException(ExceptionInformation.UNEXPECTED_FILE_CONTENT_TYPE);
    }

    public static ContentType ALL = from("*/*");
    public static ContentType PDF = from("application/pdf");
    public static ContentType DEFAULT = ALL;
    public static ContentType IMAGE_ALL = from("image/*");
}
