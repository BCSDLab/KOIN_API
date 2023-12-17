package koreatech.in.domain.Upload;

import static koreatech.in.exception.ExceptionInformation.DOMAIN_NOT_FOUND;

import java.util.Arrays;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public enum DomainEnum {


    ITEMS(), LANDS(), CIRCLES(), MARKET(), SHOPS(), MEMBERS(), OWNERS(ContentTypes.OWNER_LIMIT, ByteSize.OWNER_MAX_SIZE);

    private final ContentTypes expectContentTypes;
    private final ByteSize limitedSize;

    private final static String UPLOAD_DIRECTORY_NAME = "upload";
    private final static String SLASH = "/";
    private final static String ADMIN_PATH = "/admin";

    DomainEnum() {
        expectContentTypes = ContentTypes.DEFAULT ;
        limitedSize = ByteSize.DEFAULT;
    }

    public static DomainEnum mappingFor(String pathDomain) {
        return Arrays.stream(DomainEnum.values()).filter(domain -> domain.name().equalsIgnoreCase(pathDomain))
                .findAny()
                .orElseThrow(() -> new BaseException(DOMAIN_NOT_FOUND));
    }

    public void validateFor(MultipartFile multipartFile) {
        validates(multipartFile);

        expectContentTypes.validateAcceptable(ContentType.from(multipartFile.getContentType()));
        limitedSize.validateAcceptable(ByteSize.from(multipartFile.getSize()));
    }

    public void validateMetaData(UploadFileMetaData uploadFileMetaData) {
        expectContentTypes.validateAcceptable(ContentType.from(uploadFileMetaData.getContentType()));
        limitedSize.validateAcceptable(ByteSize.from(uploadFileMetaData.getContentLength()));
    }

    private void validates(MultipartFile multipartFile) {
        if(multipartFile == null  || multipartFile.isEmpty()) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
    }

    public String enrichDomainPath() {
        return UPLOAD_DIRECTORY_NAME + SLASH + this.name().toLowerCase();
    }

    public String enrichDomainPathForAdmin() {
        return UPLOAD_DIRECTORY_NAME + SLASH + this.name().toLowerCase() + ADMIN_PATH;
    }
}
