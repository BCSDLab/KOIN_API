package koreatech.in.service;

import java.util.ArrayList;
import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.domain.Upload.UploadFileUrl;
import koreatech.in.domain.Upload.UploadFileUrls;
import koreatech.in.domain.Upload.UploadFiles;
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
    public UploadFileResponse uploadAndGetUrl(UploadFileRequest uploadFileRequest) {
        UploadFile file = UploadFileConverter.INSTANCE.toUploadFile(uploadFileRequest);

        uploadFor(file);
        UploadFileUrl uploadedFileUrl = getUploadedFileUrl(file);

        return UploadFileConverter.INSTANCE.toUploadFileResponse(uploadedFileUrl);
    }

    @Override
    public UploadFilesResponse uploadAndGetUrls(UploadFilesRequest uploadFilesRequest) {
        UploadFiles uploadFiles = UploadFileConverter.INSTANCE.toUploadFiles(uploadFilesRequest);

        UploadFileUrls uploadFileUrls = uploadAndGetUrls(uploadFiles);

        return UploadFileConverter.INSTANCE.toUploadFilesResponse(uploadFileUrls);
    }

    private UploadFileUrls uploadAndGetUrls(UploadFiles uploadFiles) {
        UploadFileUrls uploadFileUrls = UploadFileUrls.from(new ArrayList<>());

        for (UploadFile file : uploadFiles.getUploadFiles()) {
            uploadFor(file);
            UploadFileUrl uploadedFileUrl = getUploadedFileUrl(file);

            uploadFileUrls.append(uploadedFileUrl);
        }
        return uploadFileUrls;
    }

    private void uploadFor(UploadFile uploadFile) {
        s3Util.fileUpload(bucketName, uploadFile.getFullPath(), uploadFile.getData());
    }

    private UploadFileUrl getUploadedFileUrl(UploadFile uploadFile) {
        return makeFileUrl(uploadFile.getFullPath());
    }

    private UploadFileUrl makeFileUrl(String fileFullPath) {
        return UploadFileUrl.from(domainName + UploadFileFullPath.SLASH + fileFullPath);
    }
}
