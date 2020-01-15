package koreatech.in.domain;

import koreatech.in.skillresponse.KakaoBotEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class KakaoBot {
    private static final Map<String, String> changeWord = new HashMap<String, String>() {{
        put("아침", "BREAKFAST");
        put("점심", "LUNCH");
        put("저녁", "DINNER");
        put("한기대", "koreatech");
        put("야우리", "terminal");
        put("천안역", "station");
    }};

    private static final String[] restaurantList = {"한식", "일품식", "특식", "양식", "능수관"}; //, "수박여", "2캠퍼스"

    // 통학중 셔틀 정보 : https://www.koreatech.ac.kr/kor/CMS/Contents/Contents.do?mCode=MN317
    // 방학중 셔틀 정보 : https://www.koreatech.ac.kr/kor/CMS/Contents/Contents.do?mCode=MN318
    // 2019년 겨울 방학 셔틀 : https://www.koreatech.ac.kr/kor/CMS/NoticeMgr/view.do?mCode=MN230&post_seq=25166&board_id=14
    private static final String[] expressFromKoreatechToTerminal = {
            "08:00",
            "09:35",
            "10:30",
            "11:45",
            "12:35",
            "14:00",
            "15:05",
            "16:00",
            "16:55",
            "18:05",
            "18:55",
            "20:00",
            "21:05",
            "21:55"
    };

    private static final String[] expressFromTerminalToKoreatech = {
            "07:00",
            "07:30",
            "09:00",
            "10:00",
            "10:30",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "14:30",
            "15:00",
            "16:00",
            "17:00",
            "17:50",
            "19:30",
            "20:30",
            "21:00"
    };
    private static final String[][] shuttleFromKoreatech = {
            { // 월요일
                    "14:00",
                    "18:10",
            },
            { // 화요일
                    "14:00",
                    "18:10",
            },
            { // 수요일
                    "14:00",
                    "18:10",
            },
            { // 목요일
                    "14:00",
                    "18:10",
            },
            { // 금요일
                    "14:00",
                    "18:10",
            },
            { // 토요일
            },
            { // 일요일
            }
    };
    //    private static final String[][] shuttleFromKoreatech = {
//            { // 월요일
//                    "09:10",
//                    "11:00",
//                    "14:00",
//                    "15:00",
//                    "16:00",
//                    "16:30",
//                    "17:00",
//                    "19:30",
//                    "21:00",
//                    "22:40"
//            },
//            { // 화요일
//                    "09:10",
//                    "11:00",
//                    "14:00",
//                    "15:00",
//                    "16:00",
//                    "16:30",
//                    "17:00",
//                    "19:30",
//                    "21:00",
//                    "22:40"
//            },
//            { // 수요일
//                    "09:10",
//                    "11:00",
//                    "14:00",
//                    "15:00",
//                    "16:00",
//                    "16:30",
//                    "17:00",
//                    "19:30",
//                    "21:00",
//                    "22:40"
//            },
//            { // 목요일
//                    "09:10",
//                    "11:00",
//                    "14:00",
//                    "15:00",
//                    "16:00",
//                    "16:30",
//                    "17:00",
//                    "19:30",
//                    "21:00",
//                    "22:40"
//            },
//            { // 금요일
//                    "09:10",
//                    "11:00",
//                    "14:00",
//                    "14:30",
//                    "15:00",
//                    "16:00",
//                    "16:30",
//                    "17:00",
//                    "19:30",
//                    "21:00",
//                    "22:40"
//            },
//            { // 토요일
//                    "14:00"
//            },
//            { // 일요일
//                    "17:00"
//            }
//    };
    //방학기간
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
    //통학기간
//    private static final String[][] shuttleFromTerminal = {
//            { // 월요일
//                    "08:00",
//                    "10:10",
//                    "11:25",
//                    "14:25",
//                    "16:05",
//                    "16:25",
//                    "16:55",
//                    "17:25",
//                    "18:45",
//                    "19:55",
//                    "22:00"
//            },
//            { // 화요일
//                    "08:00",
//                    "10:10",
//                    "11:25",
//                    "14:25",
//                    "16:05",
//                    "16:25",
//                    "16:55",
//                    "17:25",
//                    "18:45",
//                    "19:55",
//                    "22:00"
//            },
//            { // 수요일
//                    "08:00",
//                    "10:10",
//                    "11:25",
//                    "14:25",
//                    "16:05",
//                    "16:25",
//                    "16:55",
//                    "17:25",
//                    "18:45",
//                    "19:55",
//                    "22:00"
//            },
//            { // 목요일
//                    "08:00",
//                    "10:10",
//                    "11:25",
//                    "14:25",
//                    "16:05",
//                    "16:25",
//                    "16:55",
//                    "17:25",
//                    "18:45",
//                    "19:55",
//                    "22:00"
//            },
//            { // 금요일
//                    "08:00",
//                    "10:10",
//                    "11:25",
//                    "14:25",
//                    "16:05",
//                    "16:25",
//                    "16:55",
//                    "17:25",
//                    "18:45",
//                    "19:55",
//                    "22:00"
//            },
//            { // 토요일
//                    "14:25",
//                    "18:45"
//            },
//            { // 일요일
//                    "17:30",
//                    "21:15",
//                    "21:30"
//            }
//    };




    //방학기간
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
//통학기간
//    private static final String[][] shuttleFromStationToKoreatech = {
//            { // 월요일
//                    "08:05",
//                    "10:15",
//                    "11:30",
//                    "14:30",
//                    "16:10",
//                    "16:30",
//                    "17:00",
//                    "17:30",
//                    "18:50",
//                    "20:00",
//                    "22:05"
//            },
//            { // 화요일
//                    "08:05",
//                    "10:15",
//                    "11:30",
//                    "14:30",
//                    "16:10",
//                    "16:30",
//                    "17:00",
//                    "17:30",
//                    "18:50",
//                    "20:00",
//                    "22:05"
//            },
//            { // 수요일
//                    "08:05",
//                    "10:15",
//                    "11:30",
//                    "14:30",
//                    "16:10",
//                    "16:30",
//                    "17:00",
//                    "17:30",
//                    "18:50",
//                    "20:00",
//                    "22:05"
//            },
//            { // 목요일
//                    "08:05",
//                    "10:15",
//                    "11:30",
//                    "14:30",
//                    "16:10",
//                    "16:30",
//                    "17:00",
//                    "17:30",
//                    "18:50",
//                    "20:00",
//                    "22:05"
//            },
//            { // 금요일
//                    "08:05",
//                    "10:15",
//                    "11:30",
//                    "14:30",
//                    "16:10",
//                    "16:30",
//                    "17:00",
//                    "17:30",
//                    "18:50",
//                    "20:00",
//                    "22:05"
//            },
//            { // 토요일
//                    "14:30",
//                    "18:50"
//            },
//            { // 일요일
//                    "17:35",
//                    "21:20",
//                    "21:35"
//            }
//    };


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
//    통학기간
//    private static final String[][] shuttleFromStationToTerminal = {
//            { // 월요일
//                    "09:30",
//                    "15:20"
//            },
//            { // 화요일
//                    "09:30",
//                    "15:20"
//            },
//            { // 수요일
//                    "09:30",
//                    "15:20"
//            },
//            { // 목요일
//                    "09:30",
//                    "15:20"
//            },
//            { // 금요일
//                    "09:30",
//                    "15:20"
//            },
//            { // 토요일
//            },
//            { // 일요일
//            }
//    };

    private static final Map<String, Object> shuttleTimeTables = new HashMap<String, Object>() {{
        put("koreatech", shuttleFromKoreatech);
        put("terminal", shuttleFromTerminal);
    }};

    private static int appendTime(String[] timetable, int startIndex, int busCode, StringBuilder result) throws ParseException {
        String busName = KakaoBotEnum.BusType.values()[busCode].getTypeText(); // 학교셔틀 / 대성고속 / 시내버스

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar currentTime = Calendar.getInstance(timeZone); // 오늘 일자의 Calendar 객체

        Calendar currentBusTime = Calendar.getInstance(timeZone); // 현재 버스 시간의 Calendar 객체

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm"); // 시간표 포맷
        simpleDateFormat.setTimeZone(timeZone);

        if(timetable.length == 0) { // 오늘의 운행 시간표가 없다면 ?
            result.append(String.format("%s 미운행\n", busName));
            return -1;
        }

        for (int i = startIndex; i < timetable.length; i++) {
            currentBusTime.setTime(simpleDateFormat.parse(timetable[i]));
            currentBusTime.set(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH));
            // 오늘 일자로 시간 설정
            long timeDifference = currentBusTime.getTimeInMillis() - currentTime.getTimeInMillis();

            if (timeDifference > 0) { // 오늘 남은 버스가 존재한다면 ?
                int second = (int) (timeDifference / 1000) % 60;
                int minute = (int) ((timeDifference / (1000 * 60)) % 60);
                int hour = (int) ((timeDifference / (1000 * 60 * 60)) % 24);
                result.append(String.format("%s, %d시간 %d분 %d초 남음\n", busName, hour, minute, second));
                return i;
            }
        }
        result.append(String.format("%s 운행정보없음\n", busName));
        return -1;
    }

    public static void searchShuttleTime(String departEng, String arrivalEng, StringBuilder resultNow, StringBuilder resultNext) throws ParseException {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar currentTime = Calendar.getInstance(timeZone); // 오늘 일자의 Calendar 객체

        String[] shuttleTimeTable;
        int dayType = (currentTime.get(Calendar.DAY_OF_WEEK) + 5) % 7;

        if (!departEng.equals("station")) { // 출발지가 천안역이 아니라면 ?
            String[][] timeTable = (String[][]) shuttleTimeTables.get(departEng); // 출발지로 시간표 가져옴
            shuttleTimeTable = timeTable[dayType];
        } // 천안역이라면 ?
        else
            shuttleTimeTable = arrivalEng.equals("koreatech") ? shuttleFromStationToKoreatech[dayType] : shuttleFromStationToTerminal[dayType];
        // 도착지에 따라 다른 시간표 가져옴

        int resultIndex = appendTime(shuttleTimeTable, 0, KakaoBotEnum.BusType.SHUTTLE.ordinal(), resultNow);
        resultIndex = appendTime(shuttleTimeTable, resultIndex + 1, KakaoBotEnum.BusType.SHUTTLE.ordinal(), resultNext);
    }

    public static void searchExpressTime(String departEnglish, String arrivalEnglish, StringBuilder resultNow, StringBuilder resultNext) throws ParseException {
        String[] expressTimeTable;

        if (departEnglish.equals("koreatech") && arrivalEnglish.equals("terminal")) { // 학교에서 야우리
            expressTimeTable = expressFromKoreatechToTerminal;
        } else if (departEnglish.equals("terminal") && arrivalEnglish.equals("koreatech")) { // 야우리에서 학교
            expressTimeTable = expressFromTerminalToKoreatech;
        } else { // 그 외에는 운행하지 않으므로
            resultNow.append("대성고속 미운행\n");
            resultNext.append("대성고속 미운행\n");
            return;
        }

        int resultIndex = appendTime(expressTimeTable, 0, KakaoBotEnum.BusType.EXPRESS.ordinal(), resultNow);
        resultIndex = appendTime(expressTimeTable, resultIndex + 1, KakaoBotEnum.BusType.EXPRESS.ordinal(), resultNext);
    }

    public static String[] getRestaurantList() {
        return restaurantList;
    }

    public static String getChangedWord(String beforeWord) {
        return changeWord.get(beforeWord);
    }
}
