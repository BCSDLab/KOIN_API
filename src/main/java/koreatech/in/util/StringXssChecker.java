package koreatech.in.util;

import com.nhncorp.lucy.security.xss.LucyXssFilter;
import com.nhncorp.lucy.security.xss.XssSaxFilter;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class StringXssChecker {
    private static LucyXssFilter filter = XssSaxFilter.getInstance("lucy-xss-sax.xml");

    public static Object xssCheck(Object vo, Object clear) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        BeanInfo info = Introspector.getBeanInfo(vo.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method reader = pd.getReadMethod();
            if (reader != null) {
                //String 클래스일 경우 XSS 체크
                if (reader.invoke(vo) != null && reader.invoke(vo).getClass() == String.class) {
                    result.put(pd.getName(), filter.doFilter(reader.invoke(vo).toString()));
                    continue;
                }
                result.put(pd.getName(), reader.invoke(vo));
            }
        }
        convertMapToObject(result, clear);
        return clear;
    }

    public static Object convertMapToObject(Map<String, Object> map, Object obj) {
        String keyAttribute = null;
        String setMethodString = "set";
        String methodString = null;
        Iterator itr = map.keySet().iterator();

        while (itr.hasNext()) {
            keyAttribute = (String) itr.next();
            methodString = setMethodString + keyAttribute.substring(0, 1).toUpperCase() + keyAttribute.substring(1);
            Method[] methods = obj.getClass().getDeclaredMethods();
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
        return obj;
    }

    /**
     * @param target
     * @return target
     * @throws Exception
     *
     * 원본 소스: https://github.com/xhdtn8070/spring_boot_starter/blob/master/src/main/java/org/tikim/boot/aop/ValidXss.java
     * 변형해서 작성하였음
     *
     * 문의: 김주원_BackEnd
     *
     * xss 체크가 가능한 케이스
     * - java.lang.String
     * - java.util.List
     * - Array
     * - 커스텀 타입 (koreatech.in.dto 패키지 또는 koreatech.in.domain 패키지에 존재하는 클래스)
     */
    public static Object xssPrevent(Object target) throws Exception {
        if (target == null) {
            return target;
        }
        if (target.getClass() == String.class) {
            return filter.doFilter((String) target);
        }

        if (target instanceof List) {
            List<Object> list = (List<Object>) target;

            for (int i = 0; i < list.size(); i++) {
                list.set(i, xssPrevent(list.get(i)));
            }
        }
        if (target.getClass().isArray()) {
            Object[] array = (Object[]) target;

            for (int i = 0; i < array.length; i++) {
                array[i] = xssPrevent(array[i]);
            }
        }
        if (isCustomType(target)) {
            BeanInfo info = Introspector.getBeanInfo(target.getClass(), Object.class);

            for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                Method reader = descriptor.getReadMethod();

                if (reader != null) {
                    Object fieldObject = reader.invoke(target);

                    if (fieldObject != null) {
                        if (fieldObject.getClass() == String.class) {
                            Method setter = descriptor.getWriteMethod();
                            setter.invoke(target, xssPrevent(fieldObject));
                            continue;
                        }

                        if (fieldObject instanceof List) {
                            List<Object> list = (List<Object>) fieldObject;

                            for (int i = 0; i < list.size(); i++) {
                                list.set(i, xssPrevent(list.get(i)));
                            }
                        }
                        if (fieldObject.getClass().isArray()) {
                            Object[] array = (Object[]) fieldObject;

                            for (int i = 0; i < array.length; i++) {
                                array[i] = xssPrevent(array[i]);
                            }
                        }
                        if (isCustomType(fieldObject)) {
                            xssPrevent(fieldObject);
                        }
                    }
                }
            }
        }

        return target;
    }

    private static boolean isCustomType(Object object) {
        Package package_ = object.getClass().getPackage();

        return (package_ != null)
                && (package_.getName().startsWith("koreatech.in.dto") || package_.getName().startsWith("koreatech.in.domain"));
    }
}
