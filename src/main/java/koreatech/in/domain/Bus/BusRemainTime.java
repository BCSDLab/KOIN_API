package koreatech.in.domain.Bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BusRemainTime {

    private RemainTime nowBus;

    private RemainTime nextBus;

    private BusRemainTime(RemainTime now, RemainTime next) {
        this.nowBus = now;
        this.nextBus = next;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class RemainTime {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer busNumber;

        private Integer remainTime;
    }
}
