package koreatech.in.aop;

import koreatech.in.exception.RequestDataInvalidException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Aspect
public class ValidParameters {
    @Around(value = "@annotation(koreatech.in.annotation.ParamValid)")
    public Object validateBean(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult result = (BindingResult) arg;
                if (result.hasErrors()) {
                    List<String> violations = result.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());

                    throw new RequestDataInvalidException(violations);
                }
                break;
            }
        }
        return joinPoint.proceed();
    }
}
