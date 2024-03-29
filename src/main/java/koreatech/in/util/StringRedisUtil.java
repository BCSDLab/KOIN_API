package koreatech.in.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public abstract class StringRedisUtil<T> {
    protected ValueOperations<String, T> valOps;

    protected SetOperations<String, T> setOps;

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    public abstract void setDataAsString(String key, T data) throws IOException;

    public abstract void setDataAsString(String key, T data, Long time, TimeUnit timeUnit) throws IOException;

    public abstract T getDataAsString(String key, Class<? extends T> classType) throws IOException;

    public abstract void setDataAsSet(String key, T data) throws IOException;

    public abstract ArrayList<? extends T> getDataAsSet(String key, Class<? extends T> classType) throws IOException;
}
