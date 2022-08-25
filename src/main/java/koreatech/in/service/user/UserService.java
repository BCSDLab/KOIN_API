package koreatech.in.service.user;

import koreatech.in.controller.user.dto.request.StudentRegisterRequest;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.user.owner.Owner;
import koreatech.in.domain.user.User;
import koreatech.in.domain.user.student.Student;

import java.util.Map;

public interface UserService {
    Map<String, Object> StudentRegister(StudentRegisterRequest request, String host) throws Exception;

    Boolean authenticate(String authToken);

    Map<String, Object> changePasswordConfig(String account, String host);

    Boolean changePasswordInput(String resetToken);

    Boolean changePasswordAuthenticate(String password, String resetToken);

    Map<String, Object> withdraw() throws Exception;

    Student getStudent() throws Exception;

    Map<String,Object> updateStudentInformation(Student student) throws Exception;

    Map<String,Object> updateOwnerInformation(Owner owner) throws Exception;

    Map<String, Object> checkUserNickName(String nickname) throws Exception;

    Map<String, Object> login(User user) throws Exception;

    Map<String, Object> logout();
}
