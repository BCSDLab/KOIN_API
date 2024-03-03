package koreatech.in.annotation;

import koreatech.in.validator.OwnerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = OwnerValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnerRegistrationInfomation {
    String message() default "사업자등록번호와 첨부파일에 대해 아무것도 입력 안하거나 혹은 모두 입력해야합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String companyNumber();
    String attachmentUrls();
}
