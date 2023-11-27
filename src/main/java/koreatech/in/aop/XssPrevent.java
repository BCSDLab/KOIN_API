package koreatech.in.aop;

import java.lang.reflect.Parameter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import koreatech.in.util.StringXssChecker;

@Component
@Aspect
public class XssPrevent {
    private static final Class<RequestBody> requiredAnnotation = RequestBody.class;

    @Pointcut("@annotation(koreatech.in.annotation.XssFilter) "
        + "&& within(@org.springframework.stereotype.Controller *)")
    public void atControllerXssFilterAnnotation() {
    }

    @Around("atControllerXssFilterAnnotation()")
    public Object filterRequestBody(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] arguments = proceedingJoinPoint.getArgs();
        Parameter[] parameters = ((MethodSignature)proceedingJoinPoint.getSignature()).getMethod().getParameters();

        for (int i = 0; i < arguments.length; i++) {
            if(parameters[i].isAnnotationPresent(requiredAnnotation)) {
                arguments[i] = StringXssChecker.xssCheck(arguments[i], arguments[i].getClass().newInstance());
            }
        }

        return proceedingJoinPoint.proceed(arguments);
    }
}
