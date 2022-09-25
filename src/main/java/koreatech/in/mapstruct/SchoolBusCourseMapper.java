package koreatech.in.mapstruct;

import koreatech.in.domain.Bus.RegionEnum;
import koreatech.in.domain.Bus.SchoolBusArrivalInfo;
import koreatech.in.domain.Bus.SchoolBusCourse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SchoolBusCourseMapper {

    SchoolBusCourseMapper INSTANCE = Mappers.getMapper(SchoolBusCourseMapper.class);

    @Mappings({
            @Mapping(source = "region", target = "region", qualifiedByName = "convertRegion")
    })
    SchoolBusCourse toSchoolBusCourse(SchoolBusArrivalInfo schoolBusArrivalInfo);

    List<SchoolBusCourse> toSchoolBusCourse(List<SchoolBusArrivalInfo> schoolBusArrivalInfo);

    @Named("convertRegion")
    static String convertRegion(String region) {
        return RegionEnum.findByEnglish(region).name();
    }
}
