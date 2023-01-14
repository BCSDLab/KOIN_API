package koreatech.in.service;

import java.util.ArrayList;
import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.domain.Upload.UploadFileUrl;
import koreatech.in.domain.Upload.UploadFileUrls;
import koreatech.in.dto.upload.request.UploadFileRequest;
import koreatech.in.dto.upload.request.UploadFilesRequest;
import koreatech.in.dto.upload.response.UploadFileResponse;
import koreatech.in.dto.upload.response.UploadFilesResponse;
import koreatech.in.mapstruct.UploadFileConverter;
import koreatech.in.util.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3UploadServiceImpl implements UploadService {

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

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest uploadFileRequest) {
        UploadFileUrl uploadFileUrl = uploadAndGetUrl(uploadFileRequest);

        return UploadFileConverter.INSTANCE.toUploadFileResponse(uploadFileUrl);
    }

    @Override
    public UploadFilesResponse uploadFiles(UploadFilesRequest uploadFilesRequest) {
        UploadFileUrls uploadFileUrls = uploadAndGetUploadFileUrls(uploadFilesRequest);

        return UploadFileConverter.INSTANCE.toUploadFilesResponse(uploadFileUrls);
    }

    private UploadFileUrl makeFileUrl(String fileFullPath) {
        return UploadFileUrl.from(domainName + UploadFileFullPath.SLASH + fileFullPath);
    }

    private UploadFileUrl uploadAndGetUrl(UploadFileRequest uploadFileRequest) {
        UploadFileFullPath uploadFileFullPath = UploadFileFullPath.of(uploadFileRequest.getDomain(), uploadFileRequest.getOriginalFileName());

        UploadFile uploadFile = UploadFile.of(uploadFileFullPath, uploadFileRequest.getData());

        String fileFullPath = uploadFile.getFullPath();
        s3Util.fileUpload(bucketName, fileFullPath, uploadFile.getData());

        return makeFileUrl(fileFullPath);
    }

    private UploadFileUrls uploadAndGetUploadFileUrls(UploadFilesRequest uploadFilesRequest) {
        UploadFileUrls uploadFileUrls = UploadFileUrls.from(new ArrayList<>());

        for(UploadFileRequest uploadFileRequest: uploadFilesRequest.getUploadFileRequests()) {
            uploadFileUrls.append(uploadAndGetUrl(uploadFileRequest));
        }

        return uploadFileUrls;
    }
}
