package koreatech.in.domain.KakaoBot;

public class BusFactory {
    public static BusForTerm createBus(String termCode) {
        int termInt = Integer.parseInt(termCode);
        BusForTerm bus;
        switch (termInt % 10) {
            case 0: default:
                bus = new RegularSemester();
                break;
            case 1:
                bus = new SeasonalSemester();
                break;
            case 2:
                bus = new Vacation();
                break;
        }
        return bus;
    }
}
