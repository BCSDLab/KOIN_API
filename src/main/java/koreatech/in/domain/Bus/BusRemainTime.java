package koreatech.in.domain.Bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BusRemainTime {

    private final String busType;

    private RemainTime nowBus;

    private RemainTime nextBus;

    public BusRemainTime(String busType) {
        this.busType = busType;
    }

    private BusRemainTime(String busType, RemainTime now, RemainTime next) {
        this.busType = busType;
        this.nowBus = now;
        this.nextBus = next;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Builder {

        private String busType;

        private RemainTime nowRemainTime;

        private RemainTime nextRemainTime;

        public Builder busType(String busType) {
            this.busType = busType;
            return this;
        }

        public Builder nowRemainTime(RemainTime nowRemainTime) {
            this.nowRemainTime = nowRemainTime;
            return this;
        }

        public Builder nextRemainTime(RemainTime nextRemainTime) {
            this.nextRemainTime = nextRemainTime;
            return this;
        }

        public BusRemainTime build() {
            return new BusRemainTime(busType, nowRemainTime, nextRemainTime);
        }
    }

    @AllArgsConstructor
    @Getter
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class RemainTime {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer busNumber;

        private Integer remainTime;
    }
}
