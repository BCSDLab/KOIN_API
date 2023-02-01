package koreatech.in.schedule;

import koreatech.in.domain.Bus.CityBus;
import koreatech.in.domain.Bus.IntercityBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("busTagoSchedule")
public class BusTago {

    @Autowired
    private CityBus cityBus;

    @Autowired
    private IntercityBus intercityBus;

    @Scheduled(cron = "0 */1 * * * *")
    public void handleCityBus() {
        cityBus.cacheBusArrivalInfo();
    }

    // 하루에 한 번으로 지정하려했지만, 공공 데이터 API 호출 결과 00시에 바로 업데이트 되지 않음을 확인
    // 이전보다 더 잦게 스케줄링하도록 변경
    @Scheduled(cron = "0 */2 * * * *")
    public void handleIntercityBus() {
        intercityBus.cacheBusArrivalInfo();
    }
}
