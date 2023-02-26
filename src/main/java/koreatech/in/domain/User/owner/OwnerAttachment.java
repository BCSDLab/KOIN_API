package koreatech.in.domain.User.owner;

import java.util.Date;
import koreatech.in.domain.Upload.UploadFileFullName;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class OwnerAttachment {

    private Integer id;
    private Integer ownerId;
    private Integer shopId;
    private String fileUrl;
    private Boolean isDeleted;
    private Date updateAt;

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
