package koreatech.in.service;

import koreatech.in.dto.admin.user.request.StudentRegisterRequest;
import koreatech.in.dto.admin.user.request.UpdateUserRequest;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.dto.admin.user.response.StudentResponse;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;

import java.util.Map;

public interface UserService {
    Map<String, Object> StudentRegister(StudentRegisterRequest request, String host) throws Exception;

    Boolean authenticate(String authToken);

    Map<String, Object> changePasswordConfig(String account, String host);

    Boolean changePasswordInput(String resetToken);

    Boolean changePasswordAuthenticate(String password, String resetToken);

    Map<String, Object> withdraw() throws Exception;

    Student getStudent() throws Exception;

    StudentResponse updateStudentInformation(UpdateUserRequest request) throws Exception;

    Map<String,Object> updateOwnerInformation(Owner owner) throws Exception;

    Map<String, Object> checkUserNickName(String nickname) throws Exception;

    LoginResponse login(String account, String password) throws Exception;

    Map<String, Object> logout();
}
