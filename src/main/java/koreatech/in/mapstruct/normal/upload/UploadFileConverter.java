package koreatech.in.mapstruct.normal.upload;

import java.util.List;
import java.util.stream.Collectors;
import koreatech.in.domain.Upload.UploadFile;
import koreatech.in.domain.Upload.UploadFileFullPath;
import koreatech.in.domain.Upload.UploadFileLocation;
import koreatech.in.domain.Upload.UploadFileMetaData;
import koreatech.in.domain.Upload.UploadFiles;
import koreatech.in.domain.Upload.UploadFilesResult;
import koreatech.in.dto.normal.upload.request.PreSignedUrlRequest;
import koreatech.in.dto.normal.upload.request.UploadFileRequest;
import koreatech.in.dto.normal.upload.request.UploadFilesRequest;
import koreatech.in.dto.normal.upload.response.PreSignedUrlResponse;
import koreatech.in.dto.normal.upload.response.UploadFileResponse;
import koreatech.in.dto.normal.upload.response.UploadFilesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UploadFileConverter {

    UploadFileConverter INSTANCE = Mappers.getMapper(UploadFileConverter.class);

    UploadFileResponse toUploadFileResponse(UploadFileLocation uploadFileLocation);

    @Mappings({
//            @Mapping(source = "uploadFilesResult", target = "files", qualifiedByName = "convertUploadFilesResponse")
            @Mapping(source = "uploadFilesResult", target = "fileUrls", qualifiedByName = "convertUploadFilesResponse")
    })
    UploadFilesResponse toUploadFilesResponse(UploadFilesResult uploadFilesResult);

    @Named("convertUploadFilesResponse")
    default List<String> convertUploadFilesResponseResponse(List<UploadFileLocation> uploadFileLocations) {
        return uploadFileLocations.stream().map(UploadFileLocation::getFileUrl).collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = ".", target = "fullPath", qualifiedByName = "convertFullPath"),
            @Mapping(source = "data", target = "data")
    })
    UploadFile toUploadFile(UploadFileRequest uploadFileRequest);

    @Named("convertFullPath")
    default UploadFileFullPath convertFullPath(UploadFileRequest uploadFileRequest) {
        return UploadFileFullPath.of(uploadFileRequest.getDomain(), uploadFileRequest.getOriginalFileName());
    }

    @Mappings({
            @Mapping(source = "uploadFilesRequest", target = "uploadFiles", qualifiedByName = "convertUploadFiles")
    })
    UploadFiles toUploadFiles(UploadFilesRequest uploadFilesRequest);

    @Named("convertUploadFiles")
    default List<UploadFile> convertUploadFiles(List<UploadFileRequest> uploadFilesRequest) {
        return uploadFilesRequest.stream().map((this::toUploadFile)).collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = "preSignedPutUrl", target = "preSignedUrl"),
            @Mapping(source = "uploadFileResult", target = "fileUrl", qualifiedByName = "convertToFileUrl")
    })
    PreSignedUrlResponse toPreSignedUrlResponse(String preSignedPutUrl, UploadFileLocation uploadFileLocation);

    @Named("convertToFileUrl")
    default String convertToFileUrl(UploadFileLocation uploadFileLocation) {
        return uploadFileLocation.getFileUrl();
    }

    @Mappings({
            @Mapping(source = "fileName", target = "fileName"),
            @Mapping(source = "contentType", target = "contentType"),
            @Mapping(source = "contentLength", target = "contentLength")
    })
    UploadFileMetaData toUploadFileMetaData(PreSignedUrlRequest preSignedUrlRequest);

}
