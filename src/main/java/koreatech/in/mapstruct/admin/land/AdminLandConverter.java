package koreatech.in.mapstruct.admin.land;

import com.google.gson.Gson;
import koreatech.in.domain.BokDuck.Land;
import koreatech.in.dto.admin.land.request.CreateLandRequest;
import koreatech.in.dto.admin.land.response.LandResponse;
import koreatech.in.dto.admin.land.response.LandsResponse;
import koreatech.in.util.JsonConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminLandConverter {
    AdminLandConverter INSTANCE = Mappers.getMapper(AdminLandConverter.class);

    @Mappings({
            @Mapping(source = "name", target = "internal_name", qualifiedByName = "convertToInternalName"),
            @Mapping(source = "image_urls", target = "image_urls", qualifiedByName = "convertToImageUrlsString")
    })
    Land toLand(CreateLandRequest request);

    @Mapping(source = "image_urls", target = "image_urls", qualifiedByName = "convertToImageUrlList")
    LandResponse toLandResponse(Land land);

    LandsResponse.Land toLandsResponse$Land(Land land);

    @Named("convertToInternalName")
    default String convertToInternalName(String name) {
        return name.replace(" ","").toLowerCase();
    }

    @Named("convertToImageUrlsString")
    default String convertToImageUrlsString(List<String> imageUrls) {
        return new Gson().toJson(imageUrls);
    }

    @Named("convertToImageUrlList")
    default List<String> convertToImageUrlList(String imageUrls) {
        return JsonConstructor.parseJsonArrayWithOnlyString(imageUrls);
    }
}
