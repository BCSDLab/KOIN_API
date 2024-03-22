package koreatech.in.service;

import koreatech.in.domain.Bus.*;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Version.Version;
import koreatech.in.dto.normal.bus.BusTimetableResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.mapstruct.normal.bus.SchoolBusCourseConverter;
import koreatech.in.repository.BusRepository;
import koreatech.in.repository.VersionMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private VersionMapper versionMapper;

    @Override
    public BusRemainTime getRemainTime(String busType, String depart, String arrival) {

        try {
            Bus bus = BusTypeEnum.createBus(busType);
            return bus.getNowAndNextBusRemainTime(busType, depart, arrival);
        }  catch (DateTimeParseException e) {
            return new BusRemainTime(busType);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }
    }

    @Override
    public List<SchoolBusCourse> getCourses() {

        return SchoolBusCourseConverter.INSTANCE.toSchoolBusCourse(busRepository.findOnlyCourses());
    }

    @Override
    public List<? extends BusTimetable> getTimetable(String busType, String direction, String region) {

        if (!StringUtils.hasText(busType) || (!BusTypeEnum.EXPRESS.name().equalsIgnoreCase(busType) && !StringUtils.hasText(region))) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        try {
            Bus bus = BusTypeEnum.createBus(busType);
            return Objects.requireNonNull(bus.getTimetables(busType, direction, region));
        } catch (IllegalArgumentException e) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public List<SingleBusTime> searchTimetable(String date, String time, String depart, String arrival) {

        List<SingleBusTime> result = new ArrayList<>();
        try {
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time));
            for (BusTypeEnum busTypeEnum : BusTypeEnum.values()) {
                Bus bus = busTypeEnum.getBus();
                SingleBusTime busTime = bus.searchBusTime(busTypeEnum.name().toLowerCase(), depart, arrival, localDateTime);
                if (busTime == null) {
                    continue;
                }
                result.add(busTime);
            }
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        return result;
    }

    @Override
    public BusTimetableResponse getTimetableWithUpdatedAt(String busType, String direction, String region) {
        List<? extends BusTimetable> busTimetables = getTimetable(busType, direction, region);

        if (busType.equalsIgnoreCase("commuting")) {
            busType = "shuttle";
        }

        Version version = Optional.ofNullable(versionMapper.getVersion(busType + "_bus_timetable"))
            .orElseThrow(() -> new BaseException(ExceptionInformation.VERSION_NOT_FOUND));

        return BusTimetableResponse.builder()
            .busTimetables(busTimetables)
            .updatedAt(version.getUpdated_at())
            .build();
    }
}
