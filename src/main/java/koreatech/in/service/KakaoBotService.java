package koreatech.in.service;

public interface KakaoBotService {

    String getDiningMenus(String mealtimeKorean) throws Exception;

    String getBusRemainTime(String departKor, String arrivalKor) throws Exception;
}
