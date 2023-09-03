package koreatech.in.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Component
public class StringRedisUtilObj extends StringRedisUtil<Object> {

    private static final String redisOwnerAuthPrefix = "owner@";
    private static final String redisOwnerShopPrefix = "owner_shop@";

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        valOps = redisTemplate.opsForValue();
        setOps = redisTemplate.opsForSet();
    }

    @Override
    public void setDataAsString(String key, Object data) throws IOException {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String value = data instanceof String ? (String) data : objectMapper.writeValueAsString(data);
        valOps.set(key, value);
    }

    @Override
    public void setDataAsString(String key, Object data, Long time, TimeUnit timeUnit) throws IOException {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String value = data instanceof String ? (String) data : objectMapper.writeValueAsString(data);
        valOps.set(key, value, time, timeUnit);
    }

    private String getDataAsString(String key) {
        Optional<String> value = Optional.ofNullable((String) valOps.get(key));
        return value.orElse(null);
    }

    public <T> ArrayList<? extends T> getDataListAsString(String prefix, Class<T> classType) throws IOException {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        List<Object> objects = valOps.multiGet(keys);

        if (objects == null || objects.isEmpty()) {
            return null;
        }

        ArrayList ret = new ArrayList();

        for (Object item : objects) {
            ret.add(objectMapper.readValue((String) item, classType));
        }

        return ret;
    }

    @Override
    public Object getDataAsString(String key, Class<?> classType) throws IOException {
        if (classType == String.class) {
            return getDataAsString(key);
        }
        Optional<Object> value = Optional.ofNullable(valOps.get(key));
        if (!value.isPresent()) {
            return null;
        }
        return objectMapper.readValue((String) value.get(), classType);
    }

    @Override
    public void setDataAsSet(String key, Object data) throws IOException {
        String value = data instanceof String ? (String) data : objectMapper.writeValueAsString(data);
        setOps.add(key, value);
    }

    public ArrayList<String> getDataAsSet(String key) {
        Set<Object> value = setOps.members(key);
        if (value == null || value.isEmpty()) {
            return null;
        }
        ArrayList<String> ret = new ArrayList<>();
        value.forEach(item -> {
            ret.add((String) item);
        });
        return ret;
    }

    @Override
    public ArrayList<?> getDataAsSet(String key, Class<?> classType) throws IOException {
        if (classType == String.class) {
            return getDataAsSet(key);
        }
        Set<Object> value = setOps.members(key);
        if (value == null || value.isEmpty()) {
            return null;
        }
        ArrayList<Object> ret = new ArrayList<>();
        for (Object item : value) {
            ret.add(objectMapper.readValue((String) item, classType));
        }
        return ret;
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public static String makeOwnerKeyFor(String emailAddress) {
        return redisOwnerAuthPrefix + emailAddress;
    }

    public static String makeOwnerShopKeyFor(Integer ownerId) {
        return redisOwnerShopPrefix + ownerId;
    }
}
