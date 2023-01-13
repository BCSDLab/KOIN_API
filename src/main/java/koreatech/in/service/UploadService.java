package koreatech.in.service;

import koreatech.in.domain.Upload.UploadFileUrl;

public interface UploadService {
    void uploadFile(String uploadPath, String originalFileName, byte[] byteData);

    UploadFileUrl getUploadFileUrl(String uploadPath, String originalFileName, byte[] byteData);

}
