package koreatech.in.service;

import koreatech.in.domain.Bus.*;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.mapstruct.SchoolBusCourseMapper;
import koreatech.in.repository.BusRepository;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {

    private static final String SCHOOL_BUS_TIMETABLE_CACHE_KEY = "Tago@busTimetable.%s.%s";

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Override
    public BusRemainTime getRemainTime(String busType, String depart, String arrival) {
        if (depart.equals("koreatech") && (arrival.equals("station") || arrival.equals("terminal"))) {
        } else if (depart.equals("terminal") && (arrival.equals("station") || arrival.equals("koreatech"))) {
        } else if (depart.equals("station") && (arrival.equals("terminal") || arrival.equals("koreatech"))) {
        } else {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        try {
            Bus bus = BusTypeEnum.createBus(busType);
            return bus.getNowAndNextBusRemainTime(depart, arrival);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }
    }

    @Override
    public List<SchoolBusCourse> getCourses() {

        return SchoolBusCourseMapper.INSTANCE.toSchoolBusCourse(busRepository.findAllCourses());
    }

    @Override
    public String getTimetable(String busType, String region) {
        if (!StringUtils.hasText(busType) || (!"express".equals(busType) && !StringUtils.hasText(region))) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        String cacheKey = String.format(SCHOOL_BUS_TIMETABLE_CACHE_KEY, busType, "express".equals(busType) ? "" : region);
        try {
            return Optional.ofNullable(stringRedisUtilStr.getDataAsString(cacheKey))
                    .orElseThrow(() -> new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0)));
        } catch (IOException e) {
            throw new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0));
        }
    }
}
