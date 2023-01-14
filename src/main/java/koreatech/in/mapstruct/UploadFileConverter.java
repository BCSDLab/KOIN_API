package koreatech.in.mapstruct;

import java.util.List;
import java.util.stream.Collectors;
import koreatech.in.domain.Upload.UploadFileUrl;
import koreatech.in.domain.Upload.UploadFileUrls;
import koreatech.in.dto.upload.response.UploadFileResponse;
import koreatech.in.dto.upload.response.UploadFilesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UploadFileConverter {

    UploadFileConverter INSTANCE = Mappers.getMapper(UploadFileConverter.class);

    @Mappings({
            @Mapping(source = "fileUrl", target = "file_url")
    })
    UploadFileResponse toUploadFileResponse(UploadFileUrl uploadFileUrl);

    @Mappings({
            @Mapping(source = "uploadFileUrls", target = "file_urls", qualifiedByName = "convertUploadFileUrls")
    })
    UploadFilesResponse toUploadFilesResponse(UploadFileUrls uploadFileUrls);

    @Named("convertUploadFileUrls")
    default List<String> convertUploadFileUrls(List<UploadFileUrl> uploadFileUrls) {
        return uploadFileUrls.stream().map(UploadFileUrl::getFileUrl).collect(Collectors.toList());
    }
}
