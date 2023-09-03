package koreatech.in.service;

import java.util.ArrayList;
import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.domain.Upload.UploadFileResult;
import koreatech.in.domain.Upload.UploadFiles;
import koreatech.in.domain.Upload.UploadFilesResult;
import koreatech.in.dto.normal.upload.request.UploadableUrlRequest;
import koreatech.in.dto.normal.upload.request.UploadFileRequest;
import koreatech.in.dto.normal.upload.request.UploadFilesRequest;
import koreatech.in.dto.normal.upload.response.UploadableUrlResponse;
import koreatech.in.dto.normal.upload.response.UploadFileResponse;
import koreatech.in.dto.normal.upload.response.UploadFilesResponse;
import koreatech.in.mapstruct.normal.upload.UploadFileConverter;
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
        UploadFileResult uploadFileResult = UploadFileResult.of(domainName, file);

        return UploadFileConverter.INSTANCE.toUploadFileResponse(uploadFileResult);
    }

    @Override
    public UploadFilesResponse uploadAndGetUrls(UploadFilesRequest uploadFilesRequest) {
        UploadFiles uploadFiles = UploadFileConverter.INSTANCE.toUploadFiles(uploadFilesRequest);

        UploadFilesResult uploadFilesResult = uploadAndGetUrls(uploadFiles);

        return UploadFileConverter.INSTANCE.toUploadFilesResponse(uploadFilesResult);
    }

    public UploadableUrlResponse generatePreSignedUrl(UploadableUrlRequest uploadableUrlRequest) {
        UploadFileFullPath uploadFileFullPath = UploadFileConverter.INSTANCE.toPreSignedUrl(uploadableUrlRequest);

        String preSignedUrlForPut = s3Util.generatePreSignedUrlForPut(bucketName, uploadFileFullPath.unixValue());
        UploadFileResult uploadFileResult = UploadFileResult.of(domainName, uploadFileFullPath);
        return UploadFileConverter.INSTANCE.toPreSignedUrlResponse(preSignedUrlForPut, uploadFileResult);
    }

    private UploadFilesResult uploadAndGetUrls(UploadFiles uploadFiles) {
        UploadFilesResult uploadFilesResult = UploadFilesResult.from(new ArrayList<>());

        for (UploadFile file : uploadFiles.getUploadFiles()) {
            uploadFor(file);
            UploadFileResult uploadFileResult = UploadFileResult.of(domainName, file);

            uploadFilesResult.append(uploadFileResult);
        }
        return uploadFilesResult;
    }

    private void uploadFor(UploadFile uploadFile) {
        s3Util.fileUpload(bucketName, uploadFile.getFullPath(), uploadFile.getData());
    }
}
