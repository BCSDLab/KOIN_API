package koreatech.in.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.isContain;

public final class JsonConstructor {
    private static final Gson gson = new Gson();
    private static final JsonParser parser = new JsonParser();

    // 내부에 문자열만 존재하는 ["~~~"] 형태의 JSON을 객체로 binding, 실패 시 null 반환
    public static List<String> parseJsonArrayWithOnlyString(String jsonString) {
        if (jsonString == null) return null;
        List<String> list;
        try {
            list = gson.fromJson(jsonString, new TypeToken<List<String>>() {}.getType());
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    // 내부에 문자열만 존재하는 ["~~~"] 형태인지 검사
    public static Boolean isJsonArrayWithOnlyString(String jsonString) {
        if (jsonString == null) return false;
        JsonElement jsonElement;
        try {
            jsonElement = parser.parse(jsonString);
            if (jsonElement == null || !jsonElement.isJsonArray())
                return false;

            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject())
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // [{"~~~": "~~~", "~~~": "~~~"}] 형태의 JSON을 객체로 binding, 실패 시 null 반환
    public static List<Map<String, Object>> parseJsonArrayWithObject(JsonArray jsonArray) {
        if (jsonArray == null) return null;
        List<Map<String, Object>> list;
        try {
            for (JsonElement element : jsonArray) {
                parseJsonObject((element.getAsJsonObject()));
            }
            list = gson.fromJson(jsonArray, new TypeToken<List<Map<String, Object>>>() {}.getType());
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    // [{"~~~": "~~~", "~~~": "~~~"}] 형태의 JSON을 객체로 binding, 실패 시 null 반환
    public static List<Map<String, Object>> parseJsonArrayWithObject(String jsonString) {
        if (jsonString == null) return null;
        List<Map<String, Object>> list;
        try {
            JsonElement jsonElement = parser.parse(jsonString);
            if (jsonElement == null || !jsonElement.isJsonArray())
                return null;

            JsonArray jsonArray = jsonElement.getAsJsonArray();
            list = parseJsonArrayWithObject(jsonArray);
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    // 내부에 JSON Object만 존재하는 [{"~~~": "~~~", "~~~": "~~~"}] 형태인지 검사
    public static Boolean isJsonArrayWithOnlyObject(String jsonString) {
        if (jsonString == null) return false;
        JsonElement jsonElement;
        try {
            jsonElement = parser.parse(jsonString);
            if (jsonElement == null || !jsonElement.isJsonArray())
                return false;

            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                if (!element.isJsonObject())
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static Map<String, Object> parseJsonObject(JsonObject jsonObject, String[] arrAllowKeys) throws JsonSyntaxException, JsonIOException {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (arrAllowKeys != null && !isContain(arrAllowKeys, entry.getKey())) continue;
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive()) {
                try {
                    entry.setValue(new JsonPrimitive(entry.getValue().getAsString()));
                } catch (Exception e) {
                }
            } else if (value.isJsonObject()) {
                parseJsonObject(value.getAsJsonObject(), arrAllowKeys);
            } else if (value.isJsonArray()) {
                JsonArray jsonArray = value.getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    parseJsonObject(element.getAsJsonObject(), arrAllowKeys);
                }
            }
        }
        return gson.fromJson(jsonObject, new TypeToken<Map<String, Object>>() {}.getType());
    }

    public static Map<String, Object> parseJsonObject(JsonObject jsonObject) {
        if (jsonObject == null) return null;
        Map<String, Object> map;
        try {
            map = parseJsonObject(jsonObject, null);
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    public static Map<String, Object> parseJsonObject(String jsonString, String[] arrAllowKeys) {
        if (jsonString == null) return null;
        Map<String, Object> map;
        try {
            JsonElement jsonElement = parser.parse(jsonString);
            if (jsonElement == null || !jsonElement.isJsonObject())
                return null;

            map = parseJsonObject(jsonElement.getAsJsonObject(), arrAllowKeys);
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    public static Map<String, Object> parseJsonObject(String jsonString) {
        if (jsonString == null) return null;
        Map<String, Object> map;
        try {
            JsonElement jsonElement = parser.parse(jsonString);
            if (jsonElement == null || !jsonElement.isJsonObject())
                return null;

            map = parseJsonObject(jsonElement.getAsJsonObject(), null);
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    public static boolean isJsonObject(String jsonString) {
        if (jsonString == null) return false;
        try {
            JsonElement jsonElement = parser.parse(jsonString);
            return jsonElement != null && jsonElement.isJsonObject();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidJson(String json) {
        try {
            parser.parse(json);
        } catch (JsonSyntaxException e) {
            return false;
        }
        return true;
    }
}
