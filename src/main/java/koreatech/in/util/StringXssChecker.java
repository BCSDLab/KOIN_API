package koreatech.in.util;

import com.nhncorp.lucy.security.xss.LucyXssFilter;
import com.nhncorp.lucy.security.xss.XssSaxFilter;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class StringXssChecker {
    public static <T> T xssCheck(T vo, T clear) throws Exception {
        LucyXssFilter filter = XssSaxFilter.getInstance("lucy-xss-sax.xml");

        Map<String, Object> result = new HashMap<String, Object>();
        BeanInfo info = Introspector.getBeanInfo(vo.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method reader = pd.getReadMethod();
            if (reader != null) {
                //String 클래스일 경우 XSS 체크
                if(reader.invoke(vo) != null && reader.invoke(vo).getClass() == String.class) {
                    result.put(pd.getName(), filter.doFilter(reader.invoke(vo).toString()));
                    continue;
                }
                result.put(pd.getName(), reader.invoke(vo));
            }
        }
        convertMapToObject(result, clear);
        return clear;
    }

    public static Object convertMapToObject(Map<String,Object> map,Object obj) {
        String keyAttribute = null;
        String setMethodString = "set";
        String methodString = null;
        Iterator itr = map.keySet().iterator();

        while (itr.hasNext()) {
            keyAttribute = (String) itr.next();
            methodString = setMethodString + keyAttribute.substring(0, 1).toUpperCase() + keyAttribute.substring(1);
            Class<?> objClass = obj.getClass();
            while(objClass != null) {
                makeObjectForMap(map, obj, keyAttribute, methodString, objClass);
                objClass = objClass.getSuperclass();
            }
        }
        return obj;
    }

    private static void makeObjectForMap(Map<String, Object> map, Object obj, String keyAttribute, String methodString,
                                         Class<?> objClass) {
        Method[] methods = objClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methodString.equals(methods[i].getName())) {
                try {
                    methods[i].invoke(obj, map.get(keyAttribute));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
