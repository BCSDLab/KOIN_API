package koreatech.in.service;

import com.google.gson.JsonElement;

public interface KakaoBotService {
    String checkJsonNull(JsonElement nullableJson);

    JsonElement getNullableJsonElement(JsonElement nullableJsonElement);

    StringBuilder getHttpResponse(String URL) throws Exception;

    String crawlHaksik(String mealtimeKorean) throws Exception;

    String calculateBus(String departKorean, String arrivalKorean) throws Exception;
}
