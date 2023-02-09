package koreatech.in.domain.Upload;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContentTypes {
    ALL(from("*/*")),
    IMAGE_ALL(from("image/*"))
    ;

    private final ContentType contentType;

    public static ContentType from(String contentType) {
        try {
            return new ContentType(contentType);
        } catch (ParseException exception) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
    }
}
