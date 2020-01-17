package koreatech.in.aop;

import koreatech.in.domain.NotiSlack;
import koreatech.in.exception.ParentException;
import koreatech.in.util.SlackNotiSender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class SlackCrash {
    @Autowired
    SlackNotiSender sender;

    @AfterThrowing(pointcut = "execution(* koreatech.in.controller.admin.*Controller.*(..))", throwing = "exception")
    public void sendSlackHookingOnCrashForAdmin(JoinPoint joinPoint, Throwable exception) {
        if (exception instanceof ParentException)
            return; // Custom Exception들은 ParentException을 상속받고 있으므로 해당 Exception들만 걸러낼 수 있다.

        String errorMessage = exception.getMessage(); // PreconditionFailedException{errorMessage={error={code=0, message=Date form is invalid}}}
        String errorService = joinPoint.getTarget().getClass().getName(); // koreatech.in.service.DiningServiceImpl
        String errorMethod = joinPoint.getSignature().getName(); // getDinings
        String errorArgs = Arrays.toString(joinPoint.getArgs()); // [123]
        String errorFile = exception.getStackTrace()[0].getFileName(); // DiningServiceImpl.java
        int errorLine = exception.getStackTrace()[0].getLineNumber(); // 39
        String errorName = exception.getClass().getSimpleName(); // PreconditionFailedException

        String message = String.format("%s %s Line %d\n```%s\n\n===== [Arguments] =====\n%s```", errorName, errorFile, errorLine, errorMessage, errorArgs);


        NotiSlack slack_message = new NotiSlack();
        slack_message.setColor("danger");
        slack_message.setTitle(String.format("%s.%s", errorService, errorMethod));
        slack_message.setText(message);
        sender.noticeError(slack_message);
    }
    @AfterThrowing(pointcut = "execution(* koreatech.in.controller.*Controller.*(..))", throwing = "exception")
    public void sendSlackHookingOnCrash(JoinPoint joinPoint, Throwable exception) {
        if (exception instanceof ParentException)
            return; // Custom Exception들은 ParentException을 상속받고 있으므로 해당 Exception들만 걸러낼 수 있다.

        String errorMessage = exception.getMessage(); // PreconditionFailedException{errorMessage={error={code=0, message=Date form is invalid}}}
        String errorService = joinPoint.getTarget().getClass().getName(); // koreatech.in.service.DiningServiceImpl
        String errorMethod = joinPoint.getSignature().getName(); // getDinings
        String errorFile = exception.getStackTrace()[0].getFileName(); // DiningServiceImpl.java
        int errorLine = exception.getStackTrace()[0].getLineNumber(); // 39
        String errorName = exception.getClass().getSimpleName(); // PreconditionFailedException

        String message = String.format("%s %s Line %d\n```%s```", errorName, errorFile, errorLine, errorMessage);


        NotiSlack slack_message = new NotiSlack();
        slack_message.setColor("danger");
        slack_message.setTitle(String.format("%s.%s", errorService, errorMethod));
        slack_message.setText(message);
        sender.noticeError(slack_message);
    }
}
