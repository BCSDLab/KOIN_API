package koreatech.in.service;

import koreatech.in.dto.upload.request.UploadFileRequest;
import koreatech.in.dto.upload.request.UploadFilesRequest;
import koreatech.in.dto.upload.response.UploadFileResponse;
import koreatech.in.dto.upload.response.UploadFilesResponse;

public interface UploadService {
    UploadFileResponse uploadFil(UploadFileRequest uploadFileRequest);

    UploadFilesResponse uploadFiles(UploadFilesRequest uploadFilesRequest);
}
