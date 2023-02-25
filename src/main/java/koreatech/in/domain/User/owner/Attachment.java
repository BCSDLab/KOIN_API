package koreatech.in.domain.User.owner;

import koreatech.in.domain.Upload.UploadFileFullName;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Attachment {
    private final String fileUrl;
    private final String fileName;

    public static Attachment from(String fileUrl) {
        return new Attachment(fileUrl, fileNameFor(fileUrl));
    }

    private static String fileNameFor(String fileUrl) {
        //TODO 23.02.25. 박한수 해당 위치에서, 파일 도메인이 `owners`인지 검사를 추가해야 할지..?
        String separator = UploadFileFullPath.SLASH;
        int separateIndex = fileUrl.lastIndexOf(separator);

        if (separateIndex == UploadFileFullName.NOT_FOUND_INDEX) {
            throw new BaseException(ExceptionInformation.UPLOAD_FILE_URL_INVALID);
        }

        return fileUrl.substring(separateIndex + separator.length());
    }

}
