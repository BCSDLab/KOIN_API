package koreatech.in.aop;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.ValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

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
                    List<ObjectError> list = result.getAllErrors();
                    StringBuilder errorMessage = new StringBuilder();
                    for (ObjectError e : list) {
                        errorMessage.append(String.format("%s\n", e.getDefaultMessage()));
                    }
//                    System.out.println(errorMessage);
                    throw new ValidationException(new ErrorMessage(errorMessage.toString(), 0));
                }
                break;
            }
        }
        return joinPoint.proceed();
    }
}
