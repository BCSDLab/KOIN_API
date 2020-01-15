package koreatech.in.service;

import koreatech.in.util.SlackNotiSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SlackBotServiceImpl implements SlackBotService {
    @Autowired
    private SlackNotiSender slackNotiSender;

    @Override
    public String sendMessage(Map<String, Object> params) {
        String messageText = Optional.ofNullable(params.get("text")).map(String::valueOf).orElse("");

        return slackNotiSender.noticeQuestion(messageText);
    }
}
