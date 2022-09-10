package koreatech.in.domain.Bus;

import java.util.Comparator;
import java.util.Map;

public interface Bus {
    class SortByArrtime implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> a, Map<String, Object> b) {
            int aArrtime = Integer.parseInt(a.get("arrtime").toString());
            int bArrtime = Integer.parseInt(b.get("arrtime").toString());
            if (aArrtime == bArrtime) return 0;
            return aArrtime < bArrtime ? -1 : 1;
        }
    }

    BusRemainTime getNowAndNextBusRemainTime(String target, String depart, String arrival);
}
