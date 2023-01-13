package koreatech.in.service;

import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileUrl;

public interface UploadService {
    void uploadFile(UploadFile uploadFile);

    UploadFileUrl getUploadFileUrl(UploadFile uploadFile);

}
