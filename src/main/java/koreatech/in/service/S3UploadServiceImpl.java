package koreatech.in.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import koreatech.in.domain.Upload.DomainEnum;
import koreatech.in.domain.Upload.PreSignedUrlResult;
import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.domain.Upload.UploadFileLocation;
import koreatech.in.domain.Upload.UploadFileMetaData;
import koreatech.in.domain.Upload.UploadFiles;
import koreatech.in.domain.Upload.UploadFilesLocation;
import koreatech.in.dto.normal.upload.request.PreSignedUrlRequest;
import koreatech.in.dto.normal.upload.response.PreSignedUrlResponse;
import koreatech.in.dto.normal.upload.response.UploadFileResponse;
import koreatech.in.dto.normal.upload.response.UploadFilesResponse;
import koreatech.in.mapstruct.normal.upload.UploadFileConverter;
import koreatech.in.util.S3Util;

@Service
public class S3UploadServiceImpl implements UploadService {

    private final S3Util s3Util;

    private final String bucketName;
    private final String domainUrlPrefix;

    @Autowired
    public S3UploadServiceImpl(S3Util s3Util, @Value("${s3.bucket}") String bucketName,
                               @Value("${s3.custom_domain}") String domainUrlPrefix) {
        this.s3Util = s3Util;
        this.bucketName = bucketName;
        this.domainUrlPrefix = domainUrlPrefix;
    }

    @Override
    public UploadFileResponse uploadAndGetUrl(MultipartFile multipartFile, DomainEnum domain) throws IOException {
        domain.validateFor(multipartFile);

        UploadFile file = UploadFile.of(multipartFile, domain.enrichDomainPath());
        uploadFor(file);

        UploadFileLocation uploadFileLocation = UploadFileLocation.of(domainUrlPrefix, file);
        return UploadFileConverter.INSTANCE.toUploadFileResponse(uploadFileLocation);
    }

    @Override
    public UploadFileResponse uploadAndGetUrlForAdmin(MultipartFile multipartFile, DomainEnum domain) throws
        IOException {
        domain.validateFor(multipartFile);

        UploadFile file = UploadFile.of(multipartFile, domain.enrichDomainPathForAdmin());
        uploadFor(file);

        UploadFileLocation uploadFileLocation = UploadFileLocation.of(domainUrlPrefix, file);
        return UploadFileConverter.INSTANCE.toUploadFileResponse(uploadFileLocation);
    }

    @Override
    public UploadFilesResponse uploadAndGetUrls(List<MultipartFile> multipartFiles, DomainEnum domain)  {
        multipartFiles.forEach(domain::validateFor);

        UploadFiles uploadFiles = UploadFiles.of(multipartFiles, domain.enrichDomainPath());

        UploadFilesLocation uploadFilesLocation = uploadAndGetUrls(uploadFiles);
        return UploadFileConverter.INSTANCE.toUploadFilesResponse(uploadFilesLocation);
    }

    @Override
    public UploadFilesResponse uploadAndGetUrlsForAdmin(List<MultipartFile> multipartFiles, DomainEnum domain)  {
        multipartFiles.forEach(domain::validateFor);

        UploadFiles uploadFiles = UploadFiles.of(multipartFiles, domain.enrichDomainPathForAdmin());

        UploadFilesLocation uploadFilesLocation = uploadAndGetUrls(uploadFiles);
        return UploadFileConverter.INSTANCE.toUploadFilesResponse(uploadFilesLocation);
    }

    @Override
    public PreSignedUrlResponse generatePreSignedUrl(DomainEnum domain, PreSignedUrlRequest preSignedUrlRequest) {
        //todo 파일 업로드와 같이 리팩터링 필요
        UploadFileMetaData uploadFileMetaData = UploadFileConverter.INSTANCE.toUploadFileMetaData(preSignedUrlRequest);

        domain.validateMetaData(uploadFileMetaData);

        UploadFileFullPath uploadFileFullPath = UploadFileFullPath.of(domain.enrichDomainPath(), uploadFileMetaData.getFileName());
        PreSignedUrlResult preSignedUrlResult = s3Util.generatePreSignedUrlForPut(bucketName, uploadFileMetaData,
            uploadFileFullPath.unixValue(), new Date());

        UploadFileLocation uploadFileLocation = UploadFileLocation.of(domainUrlPrefix, uploadFileFullPath);
        return UploadFileConverter.INSTANCE.toPreSignedUrlResponse(preSignedUrlResult, uploadFileLocation);
    }

    private UploadFilesLocation uploadAndGetUrls(UploadFiles uploadFiles) {
        UploadFilesLocation uploadFilesLocation = UploadFilesLocation.from(new ArrayList<>());

        for (UploadFile file : uploadFiles.getUploadFiles()) {
            uploadFor(file);
            UploadFileLocation uploadFileLocation = UploadFileLocation.of(domainUrlPrefix, file);

            uploadFilesLocation.append(uploadFileLocation);
        }
        return uploadFilesLocation;
    }

    private void uploadFor(UploadFile uploadFile) {
        s3Util.fileUpload(bucketName, uploadFile.getFullPath(), uploadFile.getData());
    }

}
