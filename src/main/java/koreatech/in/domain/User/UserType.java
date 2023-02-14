package koreatech.in.domain.User;

import java.util.Arrays;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserType {
    OWNER(Owner.class, "사장님"),
    STUDENT(Student.class, "학생"),
    USER(User.class, "사용자");

    private final Class<? extends User> userClass;
    private final String text;

    public static UserType mappingFor(User user) {
        Class<? extends User> currClass = user.getClass();
        return mappingFor(currClass);
    }

    private static UserType mappingFor(Class<? extends User> currClass) {
        return Arrays.stream(values())
                .filter(userType -> userType.userClass.equals(currClass))
                .findAny()
                .orElseGet(() -> mappingFor((Class<? extends User>) currClass.getSuperclass()));
    }
}
