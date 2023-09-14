package koreatech.in.service;

import static koreatech.in.controller.UploadController.enrichDomainPath;

import java.util.ArrayList;
import koreatech.in.domain.Upload.DomainEnum;
import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.domain.Upload.UploadFileLocation;
import koreatech.in.domain.Upload.UploadFileMetaData;
import koreatech.in.domain.Upload.UploadFiles;
import koreatech.in.domain.Upload.UploadFilesLocation;
import koreatech.in.dto.normal.upload.request.PreSignedUrlRequest;
import koreatech.in.dto.normal.upload.request.UploadFileRequest;
import koreatech.in.dto.normal.upload.request.UploadFilesRequest;
import koreatech.in.dto.normal.upload.response.PreSignedUrlResponse;
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
        UploadFileLocation uploadFileLocation = UploadFileLocation.of(domainName, file);

        return UploadFileConverter.INSTANCE.toUploadFileResponse(uploadFileLocation);
    }

    @Override
    public UploadFilesResponse uploadAndGetUrls(UploadFilesRequest uploadFilesRequest) {
        UploadFiles uploadFiles = UploadFileConverter.INSTANCE.toUploadFiles(uploadFilesRequest);

        UploadFilesLocation uploadFilesLocation = uploadAndGetUrls(uploadFiles);

        return UploadFileConverter.INSTANCE.toUploadFilesResponse(uploadFilesLocation);
    }

    public PreSignedUrlResponse generatePreSignedUrl(String domain, PreSignedUrlRequest preSignedUrlRequest) {
        //todo 파일 업로드와 같이 리팩터링 필요
        UploadFileMetaData uploadFileMetaData = UploadFileConverter.INSTANCE.toUploadFileMetaData(preSignedUrlRequest);

        DomainEnum domainEnum = DomainEnum.mappingFor(domain);
        domainEnum.validateMetaData(uploadFileMetaData);

        UploadFileFullPath uploadFileFullPath = UploadFileFullPath.of(enrichDomainPath(domainEnum.name().toLowerCase()), uploadFileMetaData.getFileName());
        String preSignedUrlForPut = s3Util.generatePreSignedUrlForPut(bucketName,uploadFileMetaData  ,uploadFileFullPath.unixValue());

        UploadFileLocation uploadFileLocation = UploadFileLocation.of(domainName, uploadFileFullPath);
        return UploadFileConverter.INSTANCE.toPreSignedUrlResponse(preSignedUrlForPut, uploadFileLocation);
    }

    private UploadFilesLocation uploadAndGetUrls(UploadFiles uploadFiles) {
        UploadFilesLocation uploadFilesLocation = UploadFilesLocation.from(new ArrayList<>());

        for (UploadFile file : uploadFiles.getUploadFiles()) {
            uploadFor(file);
            UploadFileLocation uploadFileLocation = UploadFileLocation.of(domainName, file);

            uploadFilesLocation.append(uploadFileLocation);
        }
        return uploadFilesLocation;
    }

    private void uploadFor(UploadFile uploadFile) {
        s3Util.fileUpload(bucketName, uploadFile.getFullPath(), uploadFile.getData());
    }

}
