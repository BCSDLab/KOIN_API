package koreatech.in.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import koreatech.in.domain.Community.Board;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

//public class BoardSerializer extends JsonSerializer<Board> implements ContextualSerializer {
public class BeanSerializer extends JsonSerializer<Object> {
    private String[] blacklist;

    public BeanSerializer() {
        super();
    }

    public BeanSerializer(String[] blacklist) {
        super();
        this.blacklist = blacklist;
    }

    @Override
    public void serialize(Object o, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();

        try {
            BeanInfo info = Introspector.getBeanInfo(o.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method reader = pd.getReadMethod();
                if (reader != null) {
                    String key = pd.getName();
                    if (Arrays.asList(blacklist).contains(key)) continue;
                    Object value = reader.invoke(o);
                    generator.writeFieldName(key);
                    if(value instanceof Date) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        generator.writeObject(formatter.format(value));
                        continue;
                    }
                    generator.writeObject(value);
                }
            }
        } catch (Exception e) {
        }

        generator.writeEndObject();
    }

    public static Object getSerializedResult(Class inputType, JsonSerializer<?> ser, Object value, Class returnType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addSerializer(inputType, ser);
        objectMapper.registerModule(simpleModule);

        return objectMapper.readValue(objectMapper.writeValueAsString(value), returnType);
    }

//    @Override
//    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) {
//        System.out.println(property == null);
////        System.out.println(property.getName());
////        BeanWhitelist ann = property.getAnnotation(BeanWhitelist.class);
////        String[] whitelist = ann.whitelistArray();
////        System.out.println(ann.whitelistArray().toString());
////        for (String item : whitelist) {
////            System.out.println(item);
////        }
//        return new BoardSerializer(whitelist);
//    }
}
