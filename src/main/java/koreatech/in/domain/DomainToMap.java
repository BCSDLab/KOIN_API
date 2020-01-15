package koreatech.in.domain;

import org.apache.commons.lang.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DomainToMap {

    /**
     * vo를 map형식으로 변환해서 반환
     * @param vo VO
     * @return
     * @throws Exception
     */
    public static Map<String, Object> domainToMap(Object vo) throws Exception {
        String[] exceptKey = {"class"};
        return domainToMapWithExcept(vo, exceptKey, true);
    }

    public static Map<String, Object> domainToMap(Object vo, Boolean containNull) throws Exception {
        String[] exceptKey = {"class"};
        return domainToMapWithExcept(vo, exceptKey, containNull);
    }

    /**
     * 특정 변수를 제외해서 vo를 map형식으로 변환해서 반환.
     * @param vo VO
     * @param arrExceptKeys 제외할 property 명 리스트
     * @param containNull 변환시 null 값 포함 여부
     * @return
     * @throws Exception
     */
    public static Map<String, Object> domainToMapWithExcept(Object vo, String[] arrExceptKeys, Boolean containNull) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        BeanInfo info = Introspector.getBeanInfo(vo.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method reader = pd.getReadMethod();
            if (reader != null) {
                if(arrExceptKeys != null && arrExceptKeys.length > 0 && isContain(arrExceptKeys, pd.getName())) continue;
                if(!containNull && reader.invoke(vo) == null) continue;
                //Date 클래스일 경우 yyyy-MM-dd hh:mm:ss 형식으로 반환
                if(reader.invoke(vo) != null && reader.invoke(vo).getClass() == Date.class) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    result.put(pd.getName(), formatter.format(reader.invoke(vo)));
                    continue;
                }
                result.put(pd.getName(), reader.invoke(vo));
            }
        }
        return result;
    }
    public static Map<String, Object> domainToMapWithExcept(Object vo, String[] arrExceptKeys) throws Exception {
        return domainToMapWithExcept(vo, arrExceptKeys, true);
    }

    public static Boolean isContain(String[] arrList, String name) {
        for (String arr : arrList) {
            if (arr.equals(name))
                return true;
        }
        return false;
    }

    public static Object convertMapToObject(Map<String,Object> map,Object obj) {
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
                        System.out.println(e);
                    }
                }
            }
        }
        return obj;
    }
}
