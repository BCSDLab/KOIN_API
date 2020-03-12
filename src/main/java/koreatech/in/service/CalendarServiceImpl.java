package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.kut.Calendar;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.CalendarMapper;
import koreatech.in.skillresponse.KakaoBot;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarServiceImpl implements CalendarService {
    @Inject
    CalendarMapper calendarMapper;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    @Override
    public List<Calendar> getCalendars(String year) {
        if (year == null) {
            throw new PreconditionFailedException(new ErrorMessage("year is required", 0));
        }
        return calendarMapper.getCalendarsByYear(year);
    }

    @Override
    public Map<String, Object> getTerm() {
        String termCode = valueOps.get("termCode");

        // 데이터 존재여부 확인
        if (termCode == null) {
            throw new NotFoundException(new ErrorMessage("저장된 학기 코드가 없습니다.", 0));
        }
        return new HashMap<String, Object>() {{
            put("term", termCode);
        }};
    }

    @Override
    public String createTermForAdmin(String termCode) {
        if (!KakaoBot.TermCode.isValidTermCode(termCode)) {
            throw new PreconditionFailedException(new ErrorMessage("올바른 학기 코드가 아닙니다.", 0));
        }
        valueOps.set("termCode", termCode);
        return termCode;
    }
}
