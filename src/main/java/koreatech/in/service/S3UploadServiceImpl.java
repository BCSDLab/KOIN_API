package koreatech.in.service;

import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.domain.Upload.UploadFileUrl;
import koreatech.in.dto.upload.request.UploadFileRequest;
import koreatech.in.dto.upload.request.UploadFilesRequest;
import koreatech.in.dto.upload.response.UploadFileResponse;
import koreatech.in.dto.upload.response.UploadFilesResponse;
import koreatech.in.util.S3Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3UploadServiceImpl implements UploadService {
    private static final Logger logger = LoggerFactory.getLogger(S3UploadServiceImpl.class);

    private final S3Util s3Util;

    private final String bucketName;
    private final String domainName;

    @Autowired
    public S3UploadServiceImpl(S3Util s3Util, @Value("${s3.bucket}") String bucketName,
                               @Value("${s3.custom_domain}") String domainName) {
        this.s3Util = s3Util;
        this.bucketName = bucketName;
        this.domainName = domainName;
    }

    private UploadFileUrl makeFileUrl(String fileFullPath) {
        return UploadFileUrl.from(domainName + UploadFileFullPath.SLASH + fileFullPath);
    }

    @Override
    public UploadFileResponse uploadFil(UploadFileRequest uploadFileRequest) {
        UploadFileFullPath uploadFileFullPath = UploadFileFullPath.of(uploadFileRequest.getDomain(), uploadFileRequest.getOriginalFileName());
        UploadFile uploadFile = UploadFile.of(uploadFileFullPath, uploadFileRequest.getData());

        String fileFullPath = uploadFile.getFullPath();
        s3Util.fileUpload(bucketName, fileFullPath, uploadFile.getData());

        UploadFileUrl uploadFileUrl = makeFileUrl(fileFullPath);

        return uploadFileUrl.toDTO();
    }

    @Override
    public UploadFilesResponse uploadFiles(UploadFilesRequest uploadFilesRequest) {
        return null;
    }
}
