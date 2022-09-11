package koreatech.in.domain.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserCode {

    private static final Map<String, String> deptMap = new HashMap<String, String>() {{
        put("20", "기계공학부");
        put("35", "컴퓨터공학부");
        put("36", "컴퓨터공학부");
        put("40", "메카트로닉스공학부");
        put("61", "전기전자통신공학부");
        put("51", "디자인공학부");
        put("72", "건축공학부");
        put("74", "에너지신소재화학공학부");
        put("80", "산업경영학부");
        put("85", "고용서비스정책학과");
    }};

    public static boolean isValidatedStudentNumber(Integer identity, String studentNumber) {
        if (identity == UserIdentity.STUDENT.getIdentityType()) {
            if (studentNumber.length() != 10) {
                return false;
            }

            String admissionYear = studentNumber.substring(0, 4);
            String deptCode = studentNumber.substring(5, 7);

            if (!UserCode.deptMap.containsKey(deptCode)) {
                return false;
            }

            // 학번이 1992 ~ 신입 학번이 아니면 예외처리
            return admissionYear.compareTo("1992") >= 0 && admissionYear.compareTo(String.valueOf(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).getYear())) <= 0;
        } else if (identity.equals(UserIdentity.POSTGRADUATE.getIdentityType())) {
            return false;
        } else if (identity.equals(UserIdentity.PROFESSOR.getIdentityType())) {
            return false;
        } else if (identity.equals(UserIdentity.FACULTY.getIdentityType())) {
            return false;
        } else if (identity.equals(UserIdentity.GRADUATE.getIdentityType())) {
            return false;
        } else return !identity.equals(UserIdentity.OWNER.getIdentityType());
    }

    public static boolean isValidatedDeptNumber(String dept) {
        return deptMap.containsValue(dept);
    }

    public enum UserIdentity {
        STUDENT(0), // 재학생
        POSTGRADUATE(1), // 대학원생
        PROFESSOR(2), // 교수
        FACULTY(3), // 교직원
        GRADUATE(4), // 졸업생
        OWNER(5) // 주변상점 점주
        ;

        private int identityType;

        UserIdentity(int identityType) {
            this.identityType = identityType;
        }

        public int getIdentityType() {
            return identityType;
        }
    }
}
