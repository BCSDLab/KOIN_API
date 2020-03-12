package koreatech.in.domain.KakaoBot;

import koreatech.in.skillresponse.KakaoBot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class BusForTerm {
    // 통학중 셔틀 정보 : https://www.koreatech.ac.kr/kor/CMS/Contents/Contents.do?mCode=MN317
    // 방학중 셔틀 정보 : https://www.koreatech.ac.kr/kor/CMS/Contents/Contents.do?mCode=MN318
    // 2019년 겨울 방학 셔틀 : https://www.koreatech.ac.kr/kor/CMS/NoticeMgr/view.do?mCode=MN230&post_seq=25166&board_id=14

    protected String[] expressFromKoreatechToTerminal = {
            "08:35",
            "09:35",
            "10:35",
            "11:30",
            "12:35",
            "13:35",
            "14:35",
            "15:30",
            "16:35",
            "17:35",
            "18:35",
            "19:35",
            "20:30",
            "22:05"
    };

    protected String[] expressFromTerminalToKoreatech = {
            "07:00",
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00",
            "18:00",
            "19:00",
            "20:30",
    };

    public int appendTime(String[] timetable, int startIndex, int busCode, StringBuilder result) throws ParseException {
        String busName = KakaoBot.BusType.values()[busCode].getTypeText(); // 학교셔틀 / 대성고속 / 시내버스

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

    public void searchShuttleTime(String departEng, String arrivalEng, StringBuilder resultNow, StringBuilder resultNext) throws ParseException {
        String[] shuttleTimeTable = getShuttleTimetable(departEng, arrivalEng);

        int resultIndex = appendTime(shuttleTimeTable, 0, KakaoBot.BusType.SHUTTLE.ordinal(), resultNow);
        resultIndex = appendTime(shuttleTimeTable, resultIndex + 1, KakaoBot.BusType.SHUTTLE.ordinal(), resultNext);
    }

    public void searchExpressTime(String departEng, String arrivalEng, StringBuilder resultNow, StringBuilder resultNext) throws ParseException {
        String[] expressTimeTable;

        if (departEng.equalsIgnoreCase("koreatech") && arrivalEng.equalsIgnoreCase("terminal")) { // 학교에서 야우리
            expressTimeTable = expressFromKoreatechToTerminal;
        } else if (departEng.equalsIgnoreCase("terminal") && arrivalEng.equalsIgnoreCase("koreatech")) { // 야우리에서 학교
            expressTimeTable = expressFromTerminalToKoreatech;
        } else { // 그 외에는 운행하지 않으므로
            resultNow.append("대성고속 미운행\n");
            resultNext.append("대성고속 미운행\n");
            return;
        }

        int resultIndex = appendTime(expressTimeTable, 0, KakaoBot.BusType.EXPRESS.ordinal(), resultNow);
        resultIndex = appendTime(expressTimeTable, resultIndex + 1, KakaoBot.BusType.EXPRESS.ordinal(), resultNext);
    }

    // Hook Method
    protected abstract String[] getShuttleTimetable(String departEng, String arrivalEng);
}
