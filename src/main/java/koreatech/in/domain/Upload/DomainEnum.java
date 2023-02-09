package koreatech.in.domain.Upload;

import static koreatech.in.exception.ExceptionInformation.DOMAIN_NOT_FOUND;

import java.util.Arrays;
import javax.mail.internet.ContentType;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public enum DomainEnum {


    ITEMS, LANDS, CIRCLES, MARKET, SHOPS, MEMBERS, OWNERS(ContentTypes.IMAGE_ALL.getContentType(), ByteSize.OWNER_ATTACHMENT_MAX_SIZE);
    public static final int RIGHT_IS_BIGGER = 1;
    private final ContentType expectContentType;
    private final Long expectSize;

    DomainEnum() {
        expectContentType = ContentTypes.ALL.getContentType();
        expectSize = ByteSize.BASE_SIZE;
    }

    public static DomainEnum mappingFor(String pathDomain) {
        return Arrays.stream(DomainEnum.values()).filter(domain -> domain.name().equalsIgnoreCase(pathDomain))
                .findAny()
                .orElseThrow(() -> new BaseException(DOMAIN_NOT_FOUND));
    }

    public void validateFor(MultipartFile multipartFile) {
        validates(multipartFile);

        if(!expectContentType.match(ContentTypes.from(multipartFile.getContentType()))) {
            throw new BaseException(ExceptionInformation.UNEXPECTED_FILE_CONTENT_TYPE);
        }

        if(expectSize.compareTo(multipartFile.getSize()) == RIGHT_IS_BIGGER) {
            throw new BaseException(ExceptionInformation.FILE_SIZE_OVER);
        }
    }

    private static void validates(MultipartFile multipartFile) {
        if(multipartFile == null  || multipartFile.isEmpty()) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
    }

    private static class ByteSize {
        public static final Long _10_MB = 10_000_000L;

        public static final Long OWNER_ATTACHMENT_MAX_SIZE = _10_MB;
        public static final Long BASE_SIZE = _10_MB;
    }
}
