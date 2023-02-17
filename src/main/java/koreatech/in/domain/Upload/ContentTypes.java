package koreatech.in.domain.Upload;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.List;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContentTypes {

    private final List<ContentType> contentTypes;

    public static ContentTypes from(List<ContentType> contentTypes) {
        return new ContentTypes(contentTypes);
    }

    public void validateAcceptable(ContentType candidate) {
        contentTypes.stream()
                .filter(c -> c.isAcceptable(candidate)).findAny()
                .orElseThrow(() -> new BaseException(ExceptionInformation.UNEXPECTED_FILE_CONTENT_TYPE));
    }

    public static ContentTypes ALL = from(singletonList(ContentType.ALL));
    public static ContentTypes DEFAULT = ALL;
    public static ContentTypes IMAGE_ALL = from(singletonList(ContentType.IMAGE_ALL));
    public static ContentTypes OWNER_LIMIT = from(asList(ContentType.IMAGE_ALL, ContentType.PDF));
}
