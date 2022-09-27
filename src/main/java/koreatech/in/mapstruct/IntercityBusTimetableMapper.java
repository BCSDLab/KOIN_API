package koreatech.in.mapstruct;

import koreatech.in.domain.Bus.IntercityBusArrivalInfo;
import koreatech.in.domain.Bus.IntercityBusTimetable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Mapper
public interface IntercityBusTimetableMapper {

    IntercityBusTimetableMapper INSTANCE = Mappers.getMapper(IntercityBusTimetableMapper.class);

    @Mappings({
            @Mapping(source = "depPlandTime", target = "departure", qualifiedByName = "convertDateTime"),
            @Mapping(source = "arrPlandTime", target = "arrival", qualifiedByName = "convertDateTime"),
    })
    IntercityBusTimetable toIntercityBusTimetable(IntercityBusArrivalInfo intercityBusArrivalInfo);

    List<IntercityBusTimetable> toIntercityBusTimetable(List<IntercityBusArrivalInfo> intercityBusArrivalInfo);

    @Named("convertDateTime")
    static String convertDateTime(String datetime) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalDateTime.parse(datetime, dateTimeFormatter).format(timeFormatter);
    }
}
