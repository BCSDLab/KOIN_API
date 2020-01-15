package koreatech.in.service;

import java.util.Map;

public interface SlackBotService {
    String sendMessage(Map<String, Object> params) throws Exception;
}
