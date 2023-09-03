package koreatech.in.service;

import koreatech.in.dto.normal.upload.request.UploadableUrlRequest;
import koreatech.in.dto.normal.upload.request.UploadFileRequest;
import koreatech.in.dto.normal.upload.request.UploadFilesRequest;
import koreatech.in.dto.normal.upload.response.UploadableUrlResponse;
import koreatech.in.dto.normal.upload.response.UploadFileResponse;
import koreatech.in.dto.normal.upload.response.UploadFilesResponse;

public interface UploadService {
    UploadFileResponse uploadAndGetUrl(UploadFileRequest uploadFileRequest);

    UploadFilesResponse uploadAndGetUrls(UploadFilesRequest uploadFilesRequest);

    UploadableUrlResponse generatePreSignedUrl(UploadableUrlRequest uploadableUrlRequest);
}
