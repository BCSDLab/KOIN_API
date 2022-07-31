package koreatech.in.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Component
public class StringRedisUtilStr extends StringRedisUtil<String> {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        valOps = redisTemplate.opsForValue();
        setOps = redisTemplate.opsForSet();
    }

    @Override
    public void setDataAsString(String key, String data) throws IOException {
        valOps.set(key, data);
    }

    public String getDataAsString(String key) throws IOException {
        Optional<String> value = Optional.ofNullable(valOps.get(key));
        return value.orElse(null);
    }

    @Override
    public String getDataAsString(String key, Class<? extends String> classType) throws IOException {
        return getDataAsString(key);
    }

    @Override
    public void setDataAsSet(String key, String data) throws IOException {
        setOps.add(key, data);
    }

    public ArrayList<String> getDataAsSet(String key) throws IOException {
        Set<String> value = setOps.members(key);
        return value == null || value.isEmpty() ? null : new ArrayList<>(value);
    }

    @Override
    public ArrayList<String> getDataAsSet(String key, Class<? extends String> classType) throws IOException {
        return getDataAsSet(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
