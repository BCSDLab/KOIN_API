package koreatech.in.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import koreatech.in.service.KakaoBotService;
import koreatech.in.skillresponse.KakaoBotEnum;
import koreatech.in.skillresponse.SkillResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

@ApiIgnore
@RequestMapping(value = "/koinbot")
@Controller
public class KakaoBotController {
    @Inject
    private KakaoBotService kakaoBotService;

    @RequestMapping(value = "/dinings", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseEntity requestDinings(@RequestBody String body) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(body);

        // ['action']['params']['mealtime']
        JsonElement action = jsonElement.getAsJsonObject().get("action");
        JsonElement params = action.getAsJsonObject().get("params");
        String mealtime = params.getAsJsonObject().get("mealtime").getAsString();

        String result;
        try {
            result = kakaoBotService.crawlHaksik(mealtime);
        } catch (Exception e) {
            SkillResponse errorMsg = new SkillResponse();
            errorMsg.addSimpleText("API 오류가 발생하였습니다.");
            result = errorMsg.getSkillPayload().toString();
        }

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/buses/request", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseEntity requestBuses(@RequestBody String body) {
        SkillResponse busSkill = new SkillResponse();
        busSkill.addQujckReplies("한기대→야우리", KakaoBotEnum.QuickRepliesActionType.MESSAGE.getTypeText(), "한기대→야우리");
        busSkill.addQujckReplies("한기대→천안역", KakaoBotEnum.QuickRepliesActionType.MESSAGE.getTypeText(), "한기대→천안역");
        busSkill.addQujckReplies("야우리→한기대", KakaoBotEnum.QuickRepliesActionType.MESSAGE.getTypeText(), "야우리→한기대");
        busSkill.addQujckReplies("야우리→천안역", KakaoBotEnum.QuickRepliesActionType.MESSAGE.getTypeText(), "야우리→천안역");
        busSkill.addQujckReplies("천안역→한기대", KakaoBotEnum.QuickRepliesActionType.MESSAGE.getTypeText(), "천안역→한기대");
        busSkill.addQujckReplies("천안역→야우리", KakaoBotEnum.QuickRepliesActionType.MESSAGE.getTypeText(), "천안역→야우리");

        busSkill.addSimpleText("선택하세요!");

        String busSkillStr = busSkill.getSkillPayload().toString();

        return new ResponseEntity<String>(busSkillStr, HttpStatus.OK);
    }

    @RequestMapping(value = "/buses", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseEntity returnBuses(@RequestBody String body) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(body);

        // ['action']['params']['place'], ['action']['params']['place1']
        JsonElement action = jsonElement.getAsJsonObject().get("action");
        JsonElement params = action.getAsJsonObject().get("params");
        String depart = params.getAsJsonObject().get("place").getAsString();
        String arrival = params.getAsJsonObject().get("place1").getAsString();

        String result;
        try {
            result = kakaoBotService.calculateBus(depart, arrival);
        } catch (Exception e) {
            SkillResponse errorMsg = new SkillResponse();
            errorMsg.addSimpleText("API 오류가 발생하였습니다.");
            result = errorMsg.getSkillPayload().toString();
        }

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
