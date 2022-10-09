package koreatech.in.service;

import koreatech.in.domain.Bus.Bus;
import koreatech.in.domain.Bus.BusRemainTime;
import koreatech.in.domain.Bus.BusTypeEnum;
import koreatech.in.domain.Bus.MajorStationEnum;
import koreatech.in.domain.Dining.DiningMenuDTO;
import koreatech.in.domain.Dining.DiningTimeEnum;
import koreatech.in.skillresponse.SkillResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KakaoBotServiceImpl implements KakaoBotService {

    @Autowired
    private DiningService diningService;


    private String convertDateToStringByFormat(LocalDate at, DateTimeFormatter formatter) {
        return at.format(formatter);
    }

    @Override
    public String getDiningMenus(String diningTime) throws Exception {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        final String today = convertDateToStringByFormat(LocalDate.now(), formatter);

        List<DiningMenuDTO> dinings = diningService.getDinings(today).stream()
                .filter(dining -> dining.getType().equalsIgnoreCase(DiningTimeEnum.convertToEngName(diningTime)))
                .collect(Collectors.toList());

        StringBuilder result = new StringBuilder(String.format("[오늘의 %s 메뉴]\n", diningTime));
        if (dinings.isEmpty()) {
            result.append(String.format("금일 %s식사는 운영되지 않습니다.", diningTime));
        }
        dinings.forEach(dining -> {
            result.append(String.format("# %s\n", dining.getPlace()));
            dining.getMenu().forEach(menu -> result.append(String.format("%s\n", menu)));

            result.append(String.format("%dkcal\n", dining.getKcal())); // ex) 000kcal
            result.append(String.format("현금 %d원\n", dining.getPrice_cash())); // ex) 현금 0000원
            result.append(String.format("캐시비 %d원\n", dining.getPrice_card())); // ex) 캐시비 0000원
            result.append("────────────\n");
        });

        SkillResponse diningJson = new SkillResponse();
        diningJson.addSimpleText(result.toString());

        return diningJson.getSkillPayload().toString();
    }

    @Override
    public String getBusRemainTime(String departKor, String arrivalKor) throws IOException, ParseException {
        MajorStationEnum depart = MajorStationEnum.findByKorName(departKor);
        MajorStationEnum arrival = MajorStationEnum.findByKorName(arrivalKor);
        if (depart.name().equals(arrival.name())) {
            SkillResponse errorMessage = new SkillResponse();
            errorMessage.addSimpleText("출발지와 도착지는 같게 설정될 수 없습니다.");
            return errorMessage.getSkillPayload().toString();
        }
        StringBuilder resultNow = new StringBuilder("[바로 도착]\n");
        StringBuilder resultNext = new StringBuilder("[다음 도착]\n");

        StringBuilder nowBuses = new StringBuilder();
        StringBuilder nextBuses = new StringBuilder();
        for (BusTypeEnum busTypeEnum : BusTypeEnum.values()) {
            Bus bus = busTypeEnum.getBus();
            BusRemainTime busRemainTime = bus.getNowAndNextBusRemainTime(busTypeEnum.name().toLowerCase(), depart.getEngName(), arrival.getEngName());
            BusRemainTime.RemainTime nowRemainTime = busRemainTime.getNowBus();
            BusRemainTime.RemainTime nextRemainTime = busRemainTime.getNextBus();
            if (nowRemainTime != null) {
                nowBuses.append(String.format("%s, %d시간 %d분 %d초 남음\n",
                        busTypeEnum.getBusName(),
                        nowRemainTime.getRemainTime() / 60 / 60,
                        nowRemainTime.getRemainTime() / 60,
                        nowRemainTime.getRemainTime() % 60)
                );
            }
            if (nextRemainTime != null) {
                nextBuses.append(String.format("%s, %d시간 %d분 %d초 남음\n",
                        busTypeEnum.getBusName(),
                        nextRemainTime.getRemainTime() / 60 / 60,
                        nextRemainTime.getRemainTime() / 60,
                        nextRemainTime.getRemainTime() % 60)
                );
            }
        }

        if (nowBuses.length() == 0) {
            nowBuses.append("버스 운행정보없음\n");
        }
        if (nextBuses.length() == 0) {
            nextBuses.append("버스 운행정보없음\n");
        }
        resultNow.append(nowBuses);
        resultNext.append(nextBuses);

        SkillResponse busTime = new SkillResponse();
        busTime.addSimpleText(resultNow.toString());
        busTime.addSimpleText(resultNext.toString());

        return busTime.getSkillPayload().toString();
    }
}
