package koreatech.in.domain.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserCode {
    private static Map<String, String> code = new HashMap<String, String>() {{
        put("120", "기계공학부");
        put("135", "컴퓨터공학부");
        put("136", "컴퓨터공학부");
        put("140", "메카트로닉스공학부");
        put("161", "전기전자통신공학부");
        put("151", "디자인건축공학부");
        put("174", "에너지신소재화학공학부");
        put("180", "산업경영학부");
    }};

    public static boolean isValidatedStudentNumber(Integer identity, String studentNumber) {
        if (identity == UserIdentity.STUDENT.getIdentityType()) {
            if (studentNumber.length() != 10) {
                return false;
            }

            String admissionYear = studentNumber.substring(0, 4);
            String deptCode = studentNumber.substring(4, 7);

            if (!code.containsKey(deptCode)) {
                return false;
            }

            // 학번이 1992 ~ 신입 학번이 아니면 예외처리
            return admissionYear.compareTo("1992") >= 0 && admissionYear.compareTo((new Date()).toString().substring(0, 4)) <= 0;
        } else if (identity.equals(UserIdentity.POSTGRADUATE.getIdentityType())) {
            return false;
        } else if (identity.equals(UserIdentity.PROFESSOR.getIdentityType())) {
            return false;
        } else if (identity.equals(UserIdentity.FACULTY.getIdentityType())) {
            return false;
        } else if (identity.equals(UserIdentity.GRADUATE.getIdentityType())) {
            return false;
        } else if (identity.equals(UserIdentity.OWNER.getIdentityType())) {
            return false;
        }

        return true;
    }

    public static boolean isValidatedDeptNumber(String dept) {
        return code.containsValue(dept);
    }

    //    private static Map<String, Integer> enumIdentity = new HashMap<String, Integer>() {{
//        put("student", 0);      // 재학생
//        put("postGraduate", 1); // 대학원생
//        put("professor", 2);    // 교수
//        put("faculty", 3);      // 교직원
//        put("graduate", 4);     // 졸업생
//    }};
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
