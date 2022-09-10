package koreatech.in.service;

import koreatech.in.domain.Bus.Bus;
import koreatech.in.domain.Bus.BusFactory;
import koreatech.in.domain.Bus.BusRemainTime;
import koreatech.in.domain.Bus.SchoolBusCourse;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.BusMapper;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {
    @Autowired
    private BusMapper busMapper;

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    private static final String SCHOOL_BUS_TIMETABLE_CACHE_KEY = "Tago@busTimetable.%s.%s";

    @Override
    public BusRemainTime getRemainTime(String busType, String depart, String arrival) throws Exception {
        String target;
        if (depart.equals("koreatech") && (arrival.equals("station") || arrival.equals("terminal"))) {
            target = depart;
        } else if (depart.equals("terminal") && (arrival.equals("station") || arrival.equals("koreatech"))) {
            target = depart;
        } else if (depart.equals("station") && arrival.equals("terminal")) {
            target = depart + '-' + arrival;
        } else if (depart.equals("station") && arrival.equals("koreatech")) {
            target = depart + '-' + arrival;
        } else {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        Bus bus = BusFactory.createBus(busType);
        if (bus == null) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        return bus.getNowAndNextBusRemainTime(target, depart, arrival);
    }

    @Override
    public ArrayList<SchoolBusCourse> getCourses() {
        return busMapper.getCourses();
    }

    @Override
    public String getTimetable(String busType, String region) {
        if (!StringUtils.hasText(busType) || (!"express".equals(busType) && !StringUtils.hasText(region))) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 파라미터입니다.", 0));
        }

        String redisKey = String.format(SCHOOL_BUS_TIMETABLE_CACHE_KEY, busType, "express".equals(busType) ? "" : region);
        try {
            return Optional.ofNullable(stringRedisUtilStr.getDataAsString(redisKey))
                    .orElseThrow(() -> new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0)));
        } catch (IOException e) {
            throw new NotFoundException(new ErrorMessage("해당 버스가 존재하지 않습니다.", 0));
        }
    }
}
