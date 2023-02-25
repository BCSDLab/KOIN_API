package koreatech.in.domain.User.owner;

import koreatech.in.domain.Upload.UploadFileFullName;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    private String fileUrl;

    public static Attachment from(String fileUrl) {
        //TODO 23.02.25. https 붙일지 뗼지 [전 범위에 걸쳐서(파일 업로드, db 저장)] 박한수 해당 위치에서, 파일 도메인이 `owners`인지 검사를 추가해야 할지..?
        return new Attachment(fileUrl);
    }

    public String fileName() {
        //TODO 23.02.25. 박한수 해당 위치에서, 파일 도메인이 `owners`인지 검사를 추가해야 할지..?
        String separator = UploadFileFullPath.SLASH;
        int separateIndex = fileUrl.lastIndexOf(separator);

        if (separateIndex == UploadFileFullName.NOT_FOUND_INDEX) {
            throw new BaseException(ExceptionInformation.UPLOAD_FILE_URL_INVALID);
        }

        return fileUrl.substring(separateIndex + separator.length());
    }

}
