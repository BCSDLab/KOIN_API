package koreatech.in.domain.Bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusRemainTime {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer busNumber;

    private Integer remainTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer nextBusNumber;

    private Integer nextRemainTime;

    private BusRemainTime(RemainTime now, RemainTime next) {
        if (now != null) {
            busNumber = now.busNumber;
            remainTime = now.remainTime;
        }

        if (next != null) {
            nextBusNumber = next.busNumber;
            nextRemainTime = next.remainTime;
        }
    }

    public static class Builder {
        private RemainTime nowRemainTime;

        private RemainTime nextRemainTime;

        public Builder NowRemainTime(RemainTime nowRemainTime) {
            this.nowRemainTime = nowRemainTime;
            return this;
        }

        public Builder NextRemainTime(RemainTime nextRemainTime) {
            this.nextRemainTime = nextRemainTime;
            return this;
        }

        public BusRemainTime build() {
            return new BusRemainTime(nowRemainTime, nextRemainTime);
        }
    }

    @AllArgsConstructor
    public static class RemainTime {
        private Integer busNumber;

        private Integer remainTime;
    }
}
