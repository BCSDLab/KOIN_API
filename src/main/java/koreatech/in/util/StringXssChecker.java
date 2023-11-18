package koreatech.in.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nhncorp.lucy.security.xss.LucyXssFilter;
import com.nhncorp.lucy.security.xss.XssSaxFilter;

public class StringXssChecker {

    public static <T> T xssCheck(T vo, T clear) throws Exception {
        LucyXssFilter filter = XssSaxFilter.getInstance("lucy-xss-sax.xml");

        //clear에 할당될 결과를 정의한다.
        Map<String, Object> result = new HashMap<>();

        //빈 규약에 맞는 자바 객체에 대한 정보를 가져온다.
        BeanInfo info = Introspector.getBeanInfo(vo.getClass());

        for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
            //접근 제어자 PropertyDescriptor 중 게터만을 가져온다.
            Method reader = propertyDescriptor.getReadMethod();
            if (reader == null) {
                continue;
            }

            result.put(propertyDescriptor.getName(), getClearField(vo, filter, reader));
        }
        convertMapToObject(result, clear);
        return clear;
    }

    /**
     * vo가 String class라면 filter를 통해 필터링을 하고, 아니라면 그대로 리턴한다.
     */
    private static <T> Object getClearField(T vo, LucyXssFilter filter, Method reader) throws
        IllegalAccessException,
        InvocationTargetException {
        Object res = isStringField(vo, reader) ? filter.doFilter(reader.invoke(vo).toString()) : reader.invoke(vo);
        return res;
    }

    /**
     * vo의 reader를 실행하여 값이 있는지 확인하고, 그 결과가 String.class인지 판단한다.
     */
    private static <T> boolean isStringField(T vo, Method reader) throws
        IllegalAccessException,
        InvocationTargetException {
        return reader.invoke(vo) != null && reader.invoke(vo).getClass() == String.class;
    }

    private static Object convertMapToObject(Map<String,Object> map,Object obj) {
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
