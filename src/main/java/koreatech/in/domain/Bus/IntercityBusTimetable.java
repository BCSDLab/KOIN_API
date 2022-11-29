package koreatech.in.domain.Bus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IntercityBusTimetable extends BusTimetable implements Comparable<IntercityBusTimetable> {

    private final String departure;

    private final String arrival;

    private final int charge;

    public IntercityBusTimetable(String departure, String arrival, int charge) {
        this.departure = departure;
        this.arrival = arrival;
        this.charge = charge;
    }

    @Override
    public int compareTo(IntercityBusTimetable o) {
        return departure.compareTo(o.departure);
    }
}
