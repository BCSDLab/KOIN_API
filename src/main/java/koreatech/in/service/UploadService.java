package koreatech.in.service;

import koreatech.in.dto.upload.request.UploadFileRequest;
import koreatech.in.dto.upload.response.UploadFileResponse;

public interface UploadService {
    UploadFileResponse uploadFil(UploadFileRequest uploadFileRequest);

    UploadFileUrl getUploadFileUrl(UploadFile uploadFile);

}
