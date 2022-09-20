package koreatech.in.util;

import org.springframework.context.ApplicationContext;

public class BeanUtil {
    private static final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    public static Object getBean(String beanName) {

        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {

        return applicationContext.getBean(beanClass);
    }
}