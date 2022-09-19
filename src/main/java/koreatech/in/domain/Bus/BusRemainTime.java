package koreatech.in.domain.Bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusRemainTime {

    @JsonProperty(value = "nowBus")
    private RemainTime nowRemainTime;

    @JsonProperty(value = "nextBus")
    private RemainTime nextRemainTime;

    private BusRemainTime(RemainTime now, RemainTime next) {
        this.nowRemainTime = now;
        this.nextRemainTime = next;
    }

    public static class Builder {
        private RemainTime nowRemainTime;

        private RemainTime nextRemainTime;

        public Builder nowRemainTime(RemainTime nowRemainTime) {
            this.nowRemainTime = nowRemainTime;
            return this;
        }

        public Builder nextRemainTime(RemainTime nextRemainTime) {
            this.nextRemainTime = nextRemainTime;
            return this;
        }

        public BusRemainTime build() {
            return new BusRemainTime(nowRemainTime, nextRemainTime);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class RemainTime {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer busNumber;

        private Integer remainTime;
    }
}
