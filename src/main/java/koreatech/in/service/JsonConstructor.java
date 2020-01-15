package koreatech.in.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

import static koreatech.in.domain.DomainToMap.isContain;

public class JsonConstructor {
    private Gson gson = new Gson();
    public JsonConstructor(){}

    // ["~~~"] 형태
    public List<Map<String,Object>> arrayStringParse(String jsonString) {
        List<Map<String,Object>> list = new ArrayList<>();
        try {
            Type type = new TypeToken<List<String>>() {}.getType();
            list = gson.fromJson(jsonString, type);
        } catch (Exception e) {
        }
        return list;
    }
    public Boolean isArrayStringParse(String jsonString) {
        try {
            arrayStringParse(jsonString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // [{"~~~", "~~~"}] 형태
    public List<Map<String,Object>> arrayObjectParse(JsonArray jsonArray) {
        Gson gson = new Gson();
        Iterator<JsonElement> iterator = jsonArray.iterator();
        JsonElement jsonElement;
        Type type = new TypeToken<List<Map<String,Object>>>(){}.getType();

        for (int i = 0; i < jsonArray.size(); i++) {
            jsonElement = jsonArray.get(i);
            objectParse(jsonElement.getAsJsonObject());
        }
        return gson.fromJson(jsonArray, type);
    }

    public List<Map<String,Object>> arrayObjectParse(String jsonString) throws JsonParseException {
        List<Map<String,Object>> list = new ArrayList<>();
        JsonArray jsonArray = new JsonParser().parse(jsonString).getAsJsonArray();
        list = arrayObjectParse(jsonArray);
        return list;
    }

    public Boolean isArrayObjectParse(String jsonString) {
        try {
            arrayObjectParse(jsonString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Map<String,Object> objectParse(JsonObject jsonObject, String[] arrAllowKeys) {
        Gson gson = new Gson();
        Iterator<Map.Entry<String,JsonElement>> iterator = jsonObject.entrySet().iterator();
        Map.Entry<String,JsonElement> entry;
        Type type = new TypeToken<Map<String,Object>>(){}.getType();

        while (iterator.hasNext()) {
            entry = iterator.next();
            if (arrAllowKeys != null && !isContain(arrAllowKeys, entry.getKey())) continue;
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive()) {
                try {
                    entry.setValue(new JsonPrimitive(entry.getValue().getAsString()));
                } catch (Exception e) {}

            } else if (value.isJsonObject()) {
                objectParse(value.getAsJsonObject(), arrAllowKeys);

            } else if (value.isJsonArray()) {
                JsonArray jsonArray = value.getAsJsonArray();
                JsonElement jsonElement;
                for (int i = 0; i < jsonArray.size(); i++) {
                    jsonElement = jsonArray.get(i);
                    objectParse(jsonElement.getAsJsonObject(), arrAllowKeys);
                }
            }
        }
        return gson.fromJson(jsonObject, type);
    }

    public Map<String,Object> objectParse(JsonObject jsonObject) {
        Map<String,Object> map = new HashMap<>();
        try {
            map = objectParse(jsonObject, null);
        } catch (Exception e) {
        }
        return map;
    }

    public Map<String,Object> objectParse(String jsonString, String[] arrAllowKeys) {
        Map<String,Object> map = new HashMap<>();
        try {
            JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            map = objectParse(jsonObject, arrAllowKeys);
        } catch (Exception e) {
        }
        return map;
    }

    public Map<String,Object> objectParse(String jsonString) {
        Map<String,Object> map = new HashMap<>();
        try {
            JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            map = objectParse(jsonObject, null);
        } catch (Exception e) {
        }
        return map;
    }

    public Boolean isObjectParse(String jsonString) {
        try {
            objectParse(jsonString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
