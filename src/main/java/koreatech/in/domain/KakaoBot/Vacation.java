package koreatech.in.domain.KakaoBot;

import java.util.Calendar;
import java.util.TimeZone;

public class Vacation extends BusForTerm {
    private static final String[][] shuttleFromKoreatech = {
            { // 월요일
                    "14:00",
            },
            { // 화요일
                    "14:00",
            },
            { // 수요일
                    "14:00",
            },
            { // 목요일
                    "14:00",
            },
            { // 금요일
                    "14:00",
            },
            { // 토요일
            },
            { // 일요일
            }
    };

    private static final String[][] shuttleFromTerminal = {
            { // 월요일
                    "08:00",
                    "14:25"
            },
            { // 화요일
                    "08:00",
                    "14:25"
            },
            { // 수요일
                    "08:00",
                    "14:25"
            },
            { // 목요일
                    "08:00",
                    "14:25"
            },
            { // 금요일
                    "08:00",
                    "14:25"
            },
            { // 토요일

            },
            { // 일요일

            }
    };

    private static final String[][] shuttleFromStationToKoreatech = {
            { // 월요일
                    "08:05",
                    "14:30"
            },
            { // 화요일
                    "08:05",
                    "14:30"
            },
            { // 수요일
                    "08:05",
                    "14:30"
            },
            { // 목요일
                    "08:05",
                    "14:30"
            },
            { // 금요일
                    "08:05",
                    "14:30"
            },
            { // 토요일
            },
            { // 일요일
            }
    };

    private static final String[][] shuttleFromStationToTerminal = {
            { // 월요일
            },
            { // 화요일
            },
            { // 수요일
            },
            { // 목요일
            },
            { // 금요일
            },
            { // 토요일
            },
            { // 일요일
            }
    };

//    @Override
//    public void searchShuttleTime(String departEng, String arrivalEng, StringBuilder resultNow, StringBuilder resultNext) throws ParseException {
//        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
//        Calendar currentTime = Calendar.getInstance(timeZone); // 오늘 일자의 Calendar 객체
//
//        String[] shuttleTimeTable;
//        int dayType = (currentTime.get(Calendar.DAY_OF_WEEK) + 5) % 7;
//
//        if (!departEng.equalsIgnoreCase("station")) { // 출발지가 천안역이 아니라면?
//            shuttleTimeTable = departEng.equalsIgnoreCase("koreatech") ? shuttleFromKoreatech[dayType] : shuttleFromTerminal[dayType];
//        } else { // 천안역이라면?
//            shuttleTimeTable = arrivalEng.equalsIgnoreCase("koreatech") ? shuttleFromStationToKoreatech[dayType] : shuttleFromStationToTerminal[dayType];
//        }
//
//        int resultIndex = appendTime(shuttleTimeTable, 0, KakaoBotEnum.BusType.SHUTTLE.ordinal(), resultNow);
//        resultIndex = appendTime(shuttleTimeTable, resultIndex + 1, KakaoBotEnum.BusType.SHUTTLE.ordinal(), resultNext);
//    }

    @Override
    protected String[] getShuttleTimetable(String departEng, String arrivalEng) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar currentTime = Calendar.getInstance(timeZone); // 오늘 일자의 Calendar 객체
        int dayType = (currentTime.get(Calendar.DAY_OF_WEEK) + 5) % 7;

        if (!departEng.equalsIgnoreCase("station")) { // 출발지가 천안역이 아니라면?
            return departEng.equalsIgnoreCase("koreatech") ? shuttleFromKoreatech[dayType] : shuttleFromTerminal[dayType];
        } else { // 천안역이라면?
            return arrivalEng.equalsIgnoreCase("koreatech") ? shuttleFromStationToKoreatech[dayType] : shuttleFromStationToTerminal[dayType];
        }
    }
}
