package koreatech.in.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import koreatech.in.domain.KakaoBot.BusFactory;
import koreatech.in.domain.KakaoBot.BusForTerm;
import koreatech.in.skillresponse.KakaoBot;
import koreatech.in.skillresponse.SkillResponse;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Service
public class KakaoBotServiceImpl implements KakaoBotService {

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Override
    public String checkJsonNull(JsonElement nullableJson) {
        return nullableJson.isJsonNull() ? "" : nullableJson.getAsString();
    }

    @Override
    public JsonElement getNullableJsonElement(JsonElement nullableJsonElement) {
        return nullableJsonElement.isJsonNull() ? null : nullableJsonElement;
    }

    @Override
    public StringBuilder getHttpResponse(String URL) throws IOException {
        URL myUrl = new URL(URL);
        HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
        urlConnection.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line).append("\n");
        }

        return response;
    }

    @Override
    public String crawlHaksik(String mealtimeKorean) throws IOException {
        SimpleDateFormat dtformat = new SimpleDateFormat("yyMMdd", Locale.KOREA);
        Date current = new Date();
        String today = dtformat.format(current);

        String URL = String.format("https://api.koreatech.in/dinings?date=%s", today);

        StringBuilder response = getHttpResponse(URL);

        String mealtimeEnglish = KakaoBot.getChangedWord(mealtimeKorean);
        StringBuilder result = new StringBuilder(String.format("[오늘의 %s 메뉴]\n", mealtimeKorean));

        JsonParser jsonParser = new JsonParser();
        JsonArray diningArray = jsonParser.parse(response.toString()).getAsJsonArray();

        boolean isExist = false;

        for (String restaurant : KakaoBot.getRestaurantList()) {
            for (JsonElement dining : diningArray) { // nullable ? price_card, price_cash, kcal, menu
                JsonObject nowItemObj = dining.getAsJsonObject();
                String nowPlace = nowItemObj.get("place").getAsString();
                String nowType = nowItemObj.get("type").getAsString();
                if (restaurant.equals(nowPlace) && mealtimeEnglish.equals(nowType)) { // 식사 종류와 시간이 일치하면
                    isExist = true;
                    result.append(String.format("# %s\n", restaurant)); // ex) # 한식

                    JsonArray menuArray = nowItemObj.getAsJsonArray("menu");
                    for (JsonElement menu : menuArray) // 메뉴 넣어준 후
                        result.append(String.format("%s\n", checkJsonNull(menu)));

                    result.append(String.format("%skcal\n", checkJsonNull(nowItemObj.get("kcal")))); // ex) 000kcal
                    result.append(String.format("현금 %s원\n", checkJsonNull(nowItemObj.get("price_cash")))); // ex) 현금 0000원
                    result.append(String.format("캐시비 %s원\n", checkJsonNull(nowItemObj.get("price_card")))); // ex) 캐시비 0000원
                    result.append("────────────\n"); // 마지막 구분선
                }
            }
        }
        if (!isExist) result.append(String.format("금일 %s식사는 운영되지 않습니다.", mealtimeKorean));

        SkillResponse diningJson = new SkillResponse();
        diningJson.addSimpleText(result.toString().trim());
        String diningStr = diningJson.getSkillPayload().toString();

        return diningStr;
    }

    @Override
    public String calculateBus(String departKorean, String arrivalKorean) throws IOException, ParseException {
        String departEnglish = KakaoBot.getChangedWord(departKorean), arrivalEnglish = KakaoBot.getChangedWord(arrivalKorean); // 출발, 도착
        if(departEnglish.equals(arrivalEnglish)) { // 출발지와 도착지가 같을 경우 반환되는 메시지
            SkillResponse errorMessage = new SkillResponse();
            errorMessage.addSimpleText("출발지와 도착지는 같게 설정될 수 없습니다.");
            return errorMessage.getSkillPayload().toString();
        }
        StringBuilder resultNow = new StringBuilder("[바로 도착]\n");
        StringBuilder resultNext = new StringBuilder("[다음 도착]\n");

        BusForTerm bus = BusFactory.createBus(stringRedisUtilStr.getDataAsString("termCode"));
        // 셔틀버스 운행정보
        bus.searchShuttleTime(departEnglish, arrivalEnglish, resultNow, resultNext);
        // 대성고속 운행정보
        bus.searchExpressTime(departEnglish, arrivalEnglish, resultNow, resultNext);

        // 시내버스 운행정보
        String URL = String.format("https://api.koreatech.in/buses?depart=%s&arrival=%s", departEnglish, arrivalEnglish);
        StringBuilder response = getHttpResponse(URL);

        JsonParser jsonParser = new JsonParser();
        JsonElement cityBusElement = jsonParser.parse(response.toString());

        if (!cityBusElement.isJsonObject()) { // 코인 API에서 결과값이 없을 때는 [] 만 반환하므로

            JsonObject cityBusObject = cityBusElement.getAsJsonObject();

            Optional<Integer> busNum = Optional.ofNullable(getNullableJsonElement(cityBusObject.get("bus_number"))).map(JsonElement::getAsInt);
            Optional<Integer> remainTime = Optional.ofNullable(getNullableJsonElement(cityBusObject.get("remain_time"))).map(JsonElement::getAsInt);

            if (busNum.isPresent() && remainTime.isPresent())
                resultNow.append(String.format("%d번 버스, %d분 %d초 남음 \n", busNum.get(), remainTime.get() / 60, remainTime.get() % 60));
            else resultNow.append("시내버스 운행정보없음\n");

            Optional<Integer> nextBusNum = Optional.ofNullable(getNullableJsonElement(cityBusObject.get("next_bus_number"))).map(JsonElement::getAsInt);
            Optional<Integer> nextRemainTime = Optional.ofNullable(getNullableJsonElement(cityBusObject.get("next_remain_time"))).map(JsonElement::getAsInt);

            if (nextBusNum.isPresent() && nextRemainTime.isPresent())
                resultNext.append(String.format("%d번 버스, %d분 %d초 남음\n", nextBusNum.get(), nextRemainTime.get() / 60, nextRemainTime.get() % 60));
            else resultNext.append("시내버스 운행정보없음\n");
        } else {
            resultNow.append("시내버스 운행정보없음\n");
            resultNext.append("시내버스 운행정보없음\n");
        }

        SkillResponse busTime = new SkillResponse();
        busTime.addSimpleText(resultNow.toString().trim());
        busTime.addSimpleText(resultNext.toString().trim());
        String busTimeStr = busTime.getSkillPayload().toString();

        return busTimeStr;
    }
}
