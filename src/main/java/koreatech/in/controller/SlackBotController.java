package koreatech.in.controller;

import koreatech.in.service.SlackBotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Map;

@ApiIgnore
@Controller
public class SlackBotController {
    @Inject
    private SlackBotService slackBotService;

    @RequestMapping(value = "/slackbot/message", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    ResponseEntity sendSlackMessage(@RequestParam Map<String, Object> params) throws Exception {
        String result = slackBotService.sendMessage(params);
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
