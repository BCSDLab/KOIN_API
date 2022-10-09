package koreatech.in.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import koreatech.in.service.KakaoBotService;
import koreatech.in.skillresponse.KakaoBot;
import koreatech.in.skillresponse.SkillResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RequestMapping(value = "/koinbot")
@Controller
public class KakaoBotController {

    @Autowired
    private KakaoBotService kakaoBotService;

    @RequestMapping(value = "/dinings", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseEntity<String> requestDinings(@RequestBody String body) {

        String result;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(body);

            // ['action']['params']['dining_time']
            JsonElement action = jsonElement.getAsJsonObject().get("action");
            JsonElement params = action.getAsJsonObject().get("params");
            String diningTime = params.getAsJsonObject().get("dining_time").getAsString();

            result = kakaoBotService.getDiningMenus(diningTime);
        } catch (Exception e) {
            SkillResponse errorMsg = new SkillResponse();
            errorMsg.addSimpleText("API 오류가 발생하였습니다.");
            result = errorMsg.getSkillPayload().toString();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/buses/request", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseEntity<String> requestBuses(@RequestBody String body) {

        SkillResponse busSkill = new SkillResponse();
        busSkill.addQujckReplies("한기대→터미널", KakaoBot.QuickRepliesActionType.MESSAGE.getTypeText(), "한기대→터미널");
        busSkill.addQujckReplies("한기대→천안역", KakaoBot.QuickRepliesActionType.MESSAGE.getTypeText(), "한기대→천안역");
        busSkill.addQujckReplies("터미널→한기대", KakaoBot.QuickRepliesActionType.MESSAGE.getTypeText(), "터미널→한기대");
        busSkill.addQujckReplies("터미널→천안역", KakaoBot.QuickRepliesActionType.MESSAGE.getTypeText(), "터미널→천안역");
        busSkill.addQujckReplies("천안역→한기대", KakaoBot.QuickRepliesActionType.MESSAGE.getTypeText(), "천안역→한기대");
        busSkill.addQujckReplies("천안역→터미널", KakaoBot.QuickRepliesActionType.MESSAGE.getTypeText(), "천안역→터미널");

        busSkill.addSimpleText("선택하세요!");

        String busSkillStr = busSkill.getSkillPayload().toString();

        return new ResponseEntity<>(busSkillStr, HttpStatus.OK);
    }

    @RequestMapping(value = "/buses", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseEntity<String> returnBuses(@RequestBody String body) {

        String result;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(body);

            // ['action']['params']['depart'], ['action']['params']['arrival']
            JsonElement action = jsonElement.getAsJsonObject().get("action");
            JsonElement params = action.getAsJsonObject().get("params");
            String depart = params.getAsJsonObject().get("depart").getAsString();
            String arrival = params.getAsJsonObject().get("arrival").getAsString();

            result = kakaoBotService.getBusRemainTime(depart, arrival);
        } catch (Exception e) {
            SkillResponse errorMsg = new SkillResponse();
            errorMsg.addSimpleText("API 오류가 발생하였습니다.");
            result = errorMsg.getSkillPayload().toString();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
