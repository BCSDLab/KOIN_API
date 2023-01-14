package koreatech.in.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    enum Role { ADMIN, USER, STUDENT, OWNER, NONE }
    enum Authority { USER, CALLVAN, LAND, COMMUNITY, SHOP, VERSION, MARKET, CIRCLE, LOST, SURVEY, BCSDLAB, EVENT, NONE }

    Role role() default Role.NONE;
    Authority authority() default Authority.NONE;
}
