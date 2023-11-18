package koreatech.in.aop;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import koreatech.in.annotation.XssFilter;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.util.StringXssChecker;

@Component
@Aspect
public class XssPrevent {
    private static final Set<Class> requiredAnnotations = new LinkedHashSet<>(Arrays.asList(RequestBody.class, XssFilter.class));

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ResponseBody) "
        + "&& within(@org.springframework.stereotype.Controller *)")
    public void atResponseBodyAnnotation() {
    }

    @Around("atResponseBodyAnnotation()")
    public Object sendSlackHookingOnCrashForAdmin(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] arguments = proceedingJoinPoint.getArgs();
        Annotation[][] argumentsAnnotations = getArgumentsAnnotations(proceedingJoinPoint);

        filterArgumentsByAnnotation(arguments, argumentsAnnotations, requiredAnnotations);

        return proceedingJoinPoint.proceed(arguments);
    }

    private Annotation[][] getArgumentsAnnotations(ProceedingJoinPoint proceedingJoinPoint) {
        return ((MethodSignature)proceedingJoinPoint.getSignature()).getMethod().getParameterAnnotations();
    }
    
    private void filterArgumentsByAnnotation(Object[] arguments, Annotation[][] argumentsAnnotations,
        Collection<Class> requiredAnnotations) {
        IntStream.range(0, arguments.length)
            .filter(index -> containRequiredAnnotations(argumentsAnnotations[index], requiredAnnotations))
            .forEach(index -> filter(index, arguments));
    }

    private boolean containRequiredAnnotations(Annotation[] argumentAnnotations, Collection<Class> necessaryAnnotations) {
        return Arrays.stream(argumentAnnotations)
            .map(Annotation::annotationType)
            .collect(Collectors.toSet())
            .containsAll(necessaryAnnotations);
    }

    private void filter(int index, Object[] arguments) {
        try {
            arguments[index] = StringXssChecker.xssCheck(arguments[index], arguments[index].getClass().newInstance());
        } catch (Exception e) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }
    }

}
