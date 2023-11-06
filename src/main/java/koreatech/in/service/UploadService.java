package koreatech.in.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import koreatech.in.domain.Upload.DomainEnum;
import koreatech.in.dto.normal.upload.request.PreSignedUrlRequest;
import koreatech.in.dto.normal.upload.request.UploadFileRequest;
import koreatech.in.dto.normal.upload.request.UploadFilesRequest;
import koreatech.in.dto.normal.upload.response.PreSignedUrlResponse;
import koreatech.in.dto.normal.upload.response.UploadFileResponse;
import koreatech.in.dto.normal.upload.response.UploadFilesResponse;

public interface UploadService {
    UploadFileResponse uploadAndGetUrl(UploadFileRequest uploadFileRequest);

    UploadFileResponse  uploadAndGetUrl(MultipartFile multipartFile, DomainEnum domain) throws IOException;

    UploadFilesResponse uploadAndGetUrls(UploadFilesRequest uploadFilesRequest);

    PreSignedUrlResponse generatePreSignedUrl(String domain, PreSignedUrlRequest preSignedUrlRequest);

}
