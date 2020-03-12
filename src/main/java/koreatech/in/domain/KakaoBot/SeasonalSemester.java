package koreatech.in.domain.KakaoBot;

import java.util.Calendar;
import java.util.TimeZone;

public class SeasonalSemester extends BusForTerm {
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

    private static final String[][] shuttleFromTerminalToKoreatech = {
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

    private static final String[][] shuttleFromTerminalToStation = {
            { // 월요일
                    "14:25",
            },
            { // 화요일
                    "14:25",
            },
            { // 수요일
                    "14:25",
            },
            { // 목요일
                    "14:25",
            },
            { // 금요일
                    "14:25",
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

    @Override
    protected String[] getShuttleTimetable(String departEng, String arrivalEng) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar currentTime = Calendar.getInstance(timeZone); // 오늘 일자의 Calendar 객체
        int dayType = (currentTime.get(Calendar.DAY_OF_WEEK) + 5) % 7;

        if (departEng.equalsIgnoreCase("koreatech")) { // 출발지가 학교라면
            return shuttleFromKoreatech[dayType];
        } else if (departEng.equalsIgnoreCase("station")) { // 천안역이라면?
            return arrivalEng.equalsIgnoreCase("koreatech") ? shuttleFromStationToKoreatech[dayType] : shuttleFromStationToTerminal[dayType];
        } else { // 터미널이라면?
            return arrivalEng.equalsIgnoreCase("koreatech") ? shuttleFromTerminalToKoreatech[dayType] : shuttleFromTerminalToStation[dayType];
        }
    }
}
